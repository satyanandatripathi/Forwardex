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
data class RuleBackup(
    val rules: List<String>,
    val conditions: List<String>,
    val actions: List<String>
)

@Singleton
class BackupManager @Inject constructor() {
    fun export(rules: List<RuleEntity>, conditions: List<ConditionEntity>, actions: List<ActionEntity>): String {
        return Json.encodeToString(
            RuleBackup(
                rules = rules.map { it.toString() },
                conditions = conditions.map { it.toString() },
                actions = actions.map { it.toString() }
            )
        )
    }
}
