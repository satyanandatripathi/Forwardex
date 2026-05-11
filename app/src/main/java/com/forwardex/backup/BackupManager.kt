package com.forwardex.backup

import com.forwardex.database.ActionEntity
import com.forwardex.database.ConditionEntity
import com.forwardex.database.RuleEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
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
    private val json = Json { ignoreUnknownKeys = true }

    fun export(rules: List<RuleEntity>, conditions: List<ConditionEntity>, actions: List<ActionEntity>): String {
        return json.encodeToString(
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

    fun import(backupJson: String): RuleBackup = json.decodeFromString(backupJson)

    fun exportEncrypted(
        rules: List<RuleEntity>,
        conditions: List<ConditionEntity>,
        actions: List<ActionEntity>,
        passphrase: String
    ): String {
        val rawBackup = export(rules, conditions, actions)
        return encrypt(rawBackup, passphrase)
    }

    fun importEncrypted(cipherText: String, passphrase: String): RuleBackup {
        val rawBackup = decrypt(cipherText, passphrase)
        return import(rawBackup)
    }

    private fun encrypt(value: String, passphrase: String): String {
        val key = keyFrom(passphrase)
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    private fun decrypt(value: String, passphrase: String): String {
        val raw = Base64.getDecoder().decode(value)
        require(raw.size > 12) { "Invalid encrypted backup payload" }
        val iv = raw.copyOfRange(0, 12)
        val encrypted = raw.copyOfRange(12, raw.size)
        val key = keyFrom(passphrase)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(encrypted).toString(Charsets.UTF_8)
    }

    private fun keyFrom(passphrase: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256").digest(passphrase.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(digest, "AES")
    }
}
