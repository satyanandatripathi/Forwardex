package com.forwardex.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RulesDao {
    @Query("SELECT * FROM rules WHERE enabled = 1 ORDER BY priority DESC, id DESC")
    suspend fun getEnabledRules(): List<RuleEntity>

    @Query("SELECT * FROM rules ORDER BY updatedAt DESC")
    fun observeRules(): Flow<List<RuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRule(rule: RuleEntity): Long

    @Update
    suspend fun updateRule(rule: RuleEntity)

    @Query("UPDATE rules SET executionCount = executionCount + 1, lastExecutedAt = :executedAt, updatedAt = :executedAt WHERE id = :ruleId")
    suspend fun markExecuted(ruleId: Long, executedAt: Long)
}

@Dao
interface ConditionsDao {
    @Query("SELECT * FROM conditions WHERE ruleId = :ruleId ORDER BY id ASC")
    suspend fun getByRuleId(ruleId: Long): List<ConditionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conditions: List<ConditionEntity>)

    @Query("DELETE FROM conditions WHERE ruleId = :ruleId")
    suspend fun deleteByRuleId(ruleId: Long)
}

@Dao
interface ActionsDao {
    @Query("SELECT * FROM actions WHERE ruleId = :ruleId AND enabled = 1 ORDER BY executionOrder ASC")
    suspend fun getByRuleId(ruleId: Long): List<ActionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(actions: List<ActionEntity>)

    @Query("DELETE FROM actions WHERE ruleId = :ruleId")
    suspend fun deleteByRuleId(ruleId: Long)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM execution_history ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<HistoryEntity>

    @Query("SELECT * FROM execution_history ORDER BY timestamp DESC LIMIT 100")
    fun observeRecent(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity)

    @Query("DELETE FROM execution_history")
    suspend fun clear()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(entity: SettingsEntity)
}

@Dao
interface SimProfilesDao {
    @Query("SELECT * FROM sim_profiles ORDER BY slotIndex ASC")
    fun observeAll(): Flow<List<SimProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<SimProfileEntity>)
}

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics ORDER BY day DESC")
    fun observeAll(): Flow<List<AnalyticsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AnalyticsEntity)
}

@Dao
interface LogsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSmsLog(item: SmsLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(item: CallLogEntity)
}

@Transaction
data class RuleGraph(
    val rule: RuleEntity,
    val conditions: List<ConditionEntity>,
    val actions: List<ActionEntity>
)
