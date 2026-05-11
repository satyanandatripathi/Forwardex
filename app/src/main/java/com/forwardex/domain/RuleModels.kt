package com.forwardex.domain

import kotlinx.serialization.Serializable

@Serializable
enum class TriggerType { SMS_RECEIVED, MMS_RECEIVED, CALL_INCOMING, CALL_ANSWERED, CALL_REJECTED, MISSED_CALL, OUTGOING_SMS, DEVICE_EVENT }

@Serializable
enum class ConditionField { SENDER_NUMBER, SENDER_NAME, MESSAGE_CONTENT, RECEIVE_TIME, DAY_OF_WEEK, SIM_SLOT, CONTACT_GROUP, REGEX, OTP_DETECTED, CONTAINS_URL, LANGUAGE, LENGTH }

@Serializable
enum class ConditionOperator { CONTAINS, CONTAINS_ANY, CONTAINS_ALL, EQUALS, STARTS_WITH, ENDS_WITH, REGEX, GREATER_THAN, LESS_THAN, BETWEEN, NOT_CONTAINS }

@Serializable
enum class LogicalOperator { AND, OR }

@Serializable
enum class ActionType { SEND_SMS, SEND_EMAIL, AUTO_REPLY_SMS, HTTP_REQUEST, NOTIFICATION, CLIPBOARD, LOCAL_STORAGE }

@Serializable
enum class RuleStatus { SUCCESS, FAILED, SKIPPED }

@Serializable
data class TriggerEvent(
    val triggerType: TriggerType,
    val senderNumber: String? = null,
    val senderName: String? = null,
    val message: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val simSlot: Int? = null,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ExecutionResult(
    val success: Boolean,
    val reason: String? = null,
    val durationMs: Long = 0L,
    val forwardedTargets: List<String> = emptyList()
)

@Serializable
data class OtpExtraction(
    val code: String,
    val sender: String?,
    val expiry: String?,
    val appName: String?,
    val category: String
)
