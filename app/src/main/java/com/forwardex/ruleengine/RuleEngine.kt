package com.forwardex.ruleengine

import com.forwardex.database.HistoryEntity
import com.forwardex.domain.HistoryRepository
import com.forwardex.domain.RuleRepository
import com.forwardex.domain.RuleStatus
import com.forwardex.domain.TriggerEvent
import com.forwardex.parsers.OtpParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleEngine @Inject constructor(
    private val ruleRepository: RuleRepository,
    private val historyRepository: HistoryRepository,
    private val conditionEvaluator: ConditionEvaluator,
    private val actionExecutor: ActionExecutor,
    private val otpParser: OtpParser
) {
    private val lock = Mutex()
    private val recentKeys = ArrayDeque<String>()

    suspend fun processEvent(event: TriggerEvent) = lock.withLock {
        if (event.metadata["forwardex_source"] == "self") return@withLock
        val dedupKey = "${event.triggerType}:${event.senderNumber}:${event.message}:${event.timestamp / 30_000}"
        if (recentKeys.contains(dedupKey)) return@withLock
        recentKeys.addLast(dedupKey)
        if (recentKeys.size > 256) recentKeys.removeFirst()

        val otp = event.message?.let { otpParser.extract(it, event.senderName ?: event.senderNumber) }
        val enriched = event.copy(metadata = event.metadata + mapOf("otp" to (otp?.code != null).toString(), "otpCode" to (otp?.code ?: "")))

        val rules = ruleRepository.getEnabledRules(enriched.triggerType)
        rules.forEach { rule ->
            val now = System.currentTimeMillis()
            val cooldownUntil = (rule.lastExecutedAt ?: 0L) + rule.cooldownMs
            if (rule.cooldownMs > 0 && now < cooldownUntil) {
                historyRepository.append(
                    HistoryEntity(
                        ruleId = rule.id,
                        status = RuleStatus.SKIPPED,
                        executionTimeMs = 0,
                        payload = enriched.toString(),
                        failureReason = "Cooldown active",
                        simUsed = enriched.simSlot
                    )
                )
                return@forEach
            }
            val start = System.currentTimeMillis()
            val conditions = ruleRepository.getConditions(rule.id)
            if (!conditionEvaluator.evaluate(conditions, enriched)) return@forEach
            val actions = ruleRepository.getActions(rule.id)
            val result = executeWithRetry(
                retryCount = rule.retryCount,
                retryBackoffMs = rule.retryBackoffMs,
                block = { actionExecutor.execute(actions, enriched) }
            )
            val duration = System.currentTimeMillis() - start
            if (result.isSuccess) {
                ruleRepository.incrementExecution(rule.id)
                historyRepository.append(
                    HistoryEntity(
                        ruleId = rule.id,
                        status = RuleStatus.SUCCESS,
                        executionTimeMs = duration,
                        payload = enriched.toString(),
                        forwardedTargets = result.getOrDefault(emptyList()),
                        simUsed = enriched.simSlot
                    )
                )
            } else {
                Timber.e(result.exceptionOrNull(), "Rule %s failed", rule.id)
                historyRepository.append(
                    HistoryEntity(
                        ruleId = rule.id,
                        status = RuleStatus.FAILED,
                        executionTimeMs = duration,
                        payload = enriched.toString(),
                        failureReason = result.exceptionOrNull()?.message,
                        simUsed = enriched.simSlot
                    )
                )
            }
        }
    }

    private suspend fun executeWithRetry(
        retryCount: Int,
        retryBackoffMs: Long,
        block: suspend () -> List<String>
    ): Result<List<String>> {
        var lastFailure: Throwable? = null
        repeat(retryCount + 1) { attempt ->
            val result = runCatching { block() }
            if (result.isSuccess) return result
            lastFailure = result.exceptionOrNull()
            if (attempt < retryCount) {
                val backoff = if (retryBackoffMs > 0) retryBackoffMs * (attempt + 1) else 0L
                if (backoff > 0) delay(backoff)
            }
        }
        return Result.failure(lastFailure ?: IllegalStateException("Unknown execution failure"))
    }
}
