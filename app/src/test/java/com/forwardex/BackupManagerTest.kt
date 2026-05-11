package com.forwardex

import com.google.common.truth.Truth.assertThat
import com.forwardex.backup.BackupManager
import com.forwardex.database.ActionEntity
import com.forwardex.database.ConditionEntity
import com.forwardex.database.RuleEntity
import com.forwardex.domain.ActionType
import com.forwardex.domain.ConditionField
import com.forwardex.domain.ConditionOperator
import com.forwardex.domain.LogicalOperator
import com.forwardex.domain.TriggerType
import org.junit.Test

class BackupManagerTest {
    private val backupManager = BackupManager()

    @Test
    fun exportImportRoundTripMaintainsRuleGraph() {
        val rules = listOf(
            RuleEntity(id = 1, name = "Bank OTP", triggerType = TriggerType.SMS_RECEIVED, cooldownMs = 60000)
        )
        val conditions = listOf(
            ConditionEntity(
                id = 1,
                ruleId = 1,
                field = ConditionField.MESSAGE_CONTENT,
                operator = ConditionOperator.CONTAINS,
                values = listOf("OTP"),
                logicalOperator = LogicalOperator.AND
            )
        )
        val actions = listOf(
            ActionEntity(id = 1, ruleId = 1, actionType = ActionType.SEND_SMS, configJson = "{\"recipients\":\"+9111\"}", executionOrder = 1)
        )

        val exported = backupManager.export(rules, conditions, actions)
        val imported = backupManager.import(exported)

        assertThat(imported.rules).hasSize(1)
        assertThat(imported.conditions).hasSize(1)
        assertThat(imported.actions).hasSize(1)
        assertThat(imported.rules.first().name).isEqualTo("Bank OTP")
        assertThat(imported.actions.first().actionType).isEqualTo("SEND_SMS")
    }

    @Test
    fun encryptedExportImportRoundTripWorks() {
        val exported = backupManager.exportEncrypted(
            rules = listOf(RuleEntity(id = 1, name = "R1", triggerType = TriggerType.SMS_RECEIVED)),
            conditions = emptyList(),
            actions = emptyList(),
            passphrase = "test-passphrase"
        )

        val imported = backupManager.importEncrypted(exported, "test-passphrase")

        assertThat(imported.rules).hasSize(1)
        assertThat(imported.rules.first().name).isEqualTo("R1")
    }
}
