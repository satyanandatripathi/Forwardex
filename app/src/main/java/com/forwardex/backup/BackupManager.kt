package com.forwardex.backup

import com.forwardex.database.ActionEntity
import com.forwardex.database.ConditionEntity
import com.forwardex.database.RuleEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class RuleSnapshot(
    val id: Long,
    val name: String,
    val enabled: Boolean,
    val priority: Int,
    val triggerType: String,
    val cooldownMs: Long,
    val scheduleJson: String
)

@Serializable
data class ConditionSnapshot(
    val ruleId: Long,
    val field: String,
    val operator: String,
    val values: List<String>,
    val groupId: String,
    val logicalOperator: String
)

@Serializable
data class ActionSnapshot(
    val ruleId: Long,
    val actionType: String,
    val configJson: String,
    val executionOrder: Int
)

@Serializable
data class RuleBackup(
    val rules: List<RuleSnapshot>,
    val conditions: List<ConditionSnapshot>,
    val actions: List<ActionSnapshot>
)

@Singleton
class BackupManager @Inject constructor() {
    fun export(rules: List<RuleEntity>, conditions: List<ConditionEntity>, actions: List<ActionEntity>): String {
        return Json.encodeToString(
            RuleBackup(
                rules = rules.map {
                    RuleSnapshot(
                        id = it.id,
                        name = it.name,
                        enabled = it.enabled,
                        priority = it.priority,
                        triggerType = it.triggerType.name,
                        cooldownMs = it.cooldownMs,
                        scheduleJson = it.scheduleJson
                    )
                },
                conditions = conditions.map {
                    ConditionSnapshot(
                        ruleId = it.ruleId,
                        field = it.field.name,
                        operator = it.operator.name,
                        values = it.values,
                        groupId = it.groupId,
                        logicalOperator = it.logicalOperator.name
                    )
                },
                actions = actions.map {
                    ActionSnapshot(
                        ruleId = it.ruleId,
                        actionType = it.actionType.name,
                        configJson = it.configJson,
                        executionOrder = it.executionOrder
                    )
                }
            )
        )
    }
}
