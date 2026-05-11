package com.forwardex.domain

import com.forwardex.database.ActionEntity
import com.forwardex.database.ConditionEntity
import com.forwardex.database.HistoryEntity
import com.forwardex.database.RuleEntity
import com.forwardex.database.SettingsEntity
import com.forwardex.database.SimProfileEntity
import kotlinx.coroutines.flow.Flow

interface RuleRepository {
    fun observeRules(): Flow<List<RuleEntity>>
    suspend fun getEnabledRules(triggerType: TriggerType): List<RuleEntity>
    suspend fun getConditions(ruleId: Long): List<ConditionEntity>
    suspend fun getActions(ruleId: Long): List<ActionEntity>
    suspend fun incrementExecution(ruleId: Long)
    suspend fun saveRule(rule: RuleEntity, conditions: List<ConditionEntity>, actions: List<ActionEntity>)
}

interface HistoryRepository {
    fun observeRecent(): Flow<List<HistoryEntity>>
    suspend fun append(item: HistoryEntity)
    suspend fun getPaged(limit: Int, offset: Int): List<HistoryEntity>
}

interface SettingsRepository {
    suspend fun get(key: String): String?
    suspend fun put(key: String, value: String)
}

interface SimRepository {
    fun observeProfiles(): Flow<List<SimProfileEntity>>
    suspend fun refreshProfiles()
}
