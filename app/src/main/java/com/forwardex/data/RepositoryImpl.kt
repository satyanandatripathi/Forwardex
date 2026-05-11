package com.forwardex.data

import com.forwardex.database.ActionEntity
import com.forwardex.database.ActionsDao
import com.forwardex.database.ConditionEntity
import com.forwardex.database.ConditionsDao
import com.forwardex.database.HistoryDao
import com.forwardex.database.HistoryEntity
import com.forwardex.database.RuleEntity
import com.forwardex.database.RulesDao
import com.forwardex.database.SettingsDao
import com.forwardex.database.SettingsEntity
import com.forwardex.database.SimProfileEntity
import com.forwardex.database.SimProfilesDao
import com.forwardex.domain.HistoryRepository
import com.forwardex.domain.RuleRepository
import com.forwardex.domain.SettingsRepository
import com.forwardex.domain.SimRepository
import com.forwardex.domain.TriggerType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RuleRepositoryImpl @Inject constructor(
    private val rulesDao: RulesDao,
    private val conditionsDao: ConditionsDao,
    private val actionsDao: ActionsDao
) : RuleRepository {
    override fun observeRules(): Flow<List<RuleEntity>> = rulesDao.observeRules()

    override suspend fun getEnabledRules(triggerType: TriggerType): List<RuleEntity> =
        rulesDao.getEnabledRules().filter { it.triggerType == triggerType }

    override suspend fun getConditions(ruleId: Long): List<ConditionEntity> = conditionsDao.getByRuleId(ruleId)

    override suspend fun getActions(ruleId: Long): List<ActionEntity> = actionsDao.getByRuleId(ruleId)

    override suspend fun incrementExecution(ruleId: Long) = rulesDao.markExecuted(ruleId, System.currentTimeMillis())

    override suspend fun saveRule(rule: RuleEntity, conditions: List<ConditionEntity>, actions: List<ActionEntity>) {
        val ruleId = rulesDao.upsertRule(rule)
        conditionsDao.deleteByRuleId(ruleId)
        actionsDao.deleteByRuleId(ruleId)
        conditionsDao.insertAll(conditions.map { it.copy(ruleId = ruleId) })
        actionsDao.insertAll(actions.map { it.copy(ruleId = ruleId) })
    }
}

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {
    override fun observeRecent(): Flow<List<HistoryEntity>> = historyDao.observeRecent()
    override suspend fun append(item: HistoryEntity) = historyDao.insert(item)
    override suspend fun getPaged(limit: Int, offset: Int): List<HistoryEntity> = historyDao.getPaged(limit, offset)
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsDao: SettingsDao
) : SettingsRepository {
    override suspend fun get(key: String): String? = settingsDao.get(key)?.value
    override suspend fun put(key: String, value: String) = settingsDao.put(SettingsEntity(key, value))
}

@Singleton
class SimRepositoryImpl @Inject constructor(
    private val simProfilesDao: SimProfilesDao
) : SimRepository {
    override fun observeProfiles() = simProfilesDao.observeAll()

    override suspend fun refreshProfiles() {
        simProfilesDao.upsertAll(
            listOf(
                SimProfileEntity(slotIndex = 0, carrierName = "SIM 1", displayName = "Primary"),
                SimProfileEntity(slotIndex = 1, carrierName = "SIM 2", displayName = "Secondary")
            )
        )
    }
}
