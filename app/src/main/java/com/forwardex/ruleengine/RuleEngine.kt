package com.forwardex.ruleengine

import com.forwardex.database.HistoryEntity
import com.forwardex.domain.HistoryRepository
import com.forwardex.domain.RuleRepository
import com.forwardex.domain.RuleStatus
import com.forwardex.domain.TriggerEvent
import com.forwardex.parsers.OtpParser
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
        val dedupKey = "${event.triggerType}:${event.senderNumber}:${event.message}:${event.timestamp / 30_000}"
        if (recentKeys.contains(dedupKey)) return@withLock
        recentKeys.addLast(dedupKey)
        if (recentKeys.size > 256) recentKeys.removeFirst()

        val otp = event.message?.let { otpParser.extract(it, event.senderName ?: event.senderNumber) }
        val enriched = event.copy(metadata = event.metadata + mapOf("otp" to (otp?.code != null).toString(), "otpCode" to (otp?.code ?: "")))

        val rules = ruleRepository.getEnabledRules(enriched.triggerType)
        rules.forEach { rule ->
            val start = System.currentTimeMillis()
            val conditions = ruleRepository.getConditions(rule.id)
            if (!conditionEvaluator.evaluate(conditions, enriched)) return@forEach
            val actions = ruleRepository.getActions(rule.id)
            val result = runCatching { actionExecutor.execute(actions, enriched) }
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
}
