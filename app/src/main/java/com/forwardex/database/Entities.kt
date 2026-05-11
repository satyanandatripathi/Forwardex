package com.forwardex.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.forwardex.domain.ActionType
import com.forwardex.domain.ConditionField
import com.forwardex.domain.ConditionOperator
import com.forwardex.domain.LogicalOperator
import com.forwardex.domain.RuleStatus
import com.forwardex.domain.TriggerType

@Entity(tableName = "rules")
data class RuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val enabled: Boolean = true,
    val priority: Int = 0,
    val triggerType: TriggerType,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val cooldownMs: Long = 0,
    val scheduleJson: String = "",
    val retryCount: Int = 0,
    val retryBackoffMs: Long = 0,
    val executionCount: Long = 0,
    val lastExecutedAt: Long? = null
)

@Entity(
    tableName = "conditions",
    foreignKeys = [ForeignKey(entity = RuleEntity::class, parentColumns = ["id"], childColumns = ["ruleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("ruleId")]
)
data class ConditionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val field: ConditionField,
    val operator: ConditionOperator,
    val values: List<String>,
    val groupId: String = "root",
    val logicalOperator: LogicalOperator = LogicalOperator.AND
)

@Entity(
    tableName = "actions",
    foreignKeys = [ForeignKey(entity = RuleEntity::class, parentColumns = ["id"], childColumns = ["ruleId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("ruleId")]
)
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val actionType: ActionType,
    val configJson: String,
    val executionOrder: Int,
    val enabled: Boolean = true
)

@Entity(tableName = "execution_history", indices = [Index("ruleId"), Index("timestamp")])
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ruleId: Long,
    val status: RuleStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val executionTimeMs: Long,
    val payload: String,
    val failureReason: String? = null,
    val simUsed: Int? = null,
    val forwardedTargets: List<String> = emptyList()
)

@Entity(tableName = "sms_logs")
data class SmsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val message: String,
    val timestamp: Long,
    val simSlot: Int?
)

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val eventType: String,
    val timestamp: Long
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "sim_profiles")
data class SimProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val slotIndex: Int,
    val carrierName: String,
    val displayName: String,
    val subscriptionId: Int? = null,
    val isEsim: Boolean = false
)

@Entity(tableName = "analytics")
data class AnalyticsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val metric: String,
    val value: Long,
    val day: String
)
