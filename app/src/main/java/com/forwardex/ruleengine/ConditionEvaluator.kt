package com.forwardex.ruleengine

import com.forwardex.database.ConditionEntity
import com.forwardex.domain.ConditionField
import com.forwardex.domain.ConditionOperator
import com.forwardex.domain.LogicalOperator
import com.forwardex.domain.TriggerEvent
import com.forwardex.parsers.UrlParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConditionEvaluator @Inject constructor(
    private val urlParser: UrlParser
) {
    fun evaluate(conditions: List<ConditionEntity>, event: TriggerEvent): Boolean {
        if (conditions.isEmpty()) return true
        val grouped = conditions.groupBy { it.groupId }
        return evaluateGroup(grouped["root"].orEmpty(), event)
    }

    private fun evaluateGroup(conditions: List<ConditionEntity>, event: TriggerEvent): Boolean {
        if (conditions.isEmpty()) return true
        var result = conditionMatches(conditions.first(), event)
        for (condition in conditions.drop(1)) {
            val current = conditionMatches(condition, event)
            result = if (condition.logicalOperator == LogicalOperator.AND) result && current else result || current
        }
        return result
    }

    private fun conditionMatches(condition: ConditionEntity, event: TriggerEvent): Boolean {
        val left = when (condition.field) {
            ConditionField.SENDER_NUMBER -> event.senderNumber.orEmpty()
            ConditionField.SENDER_NAME -> event.senderName.orEmpty()
            ConditionField.MESSAGE_CONTENT -> event.message.orEmpty()
            ConditionField.RECEIVE_TIME -> event.timestamp.toString()
            ConditionField.DAY_OF_WEEK -> java.time.Instant.ofEpochMilli(event.timestamp).atZone(java.time.ZoneId.systemDefault()).dayOfWeek.name
            ConditionField.SIM_SLOT -> event.simSlot?.toString().orEmpty()
            ConditionField.CONTACT_GROUP -> event.metadata["contactGroup"].orEmpty()
            ConditionField.REGEX -> event.message.orEmpty()
            ConditionField.OTP_DETECTED -> event.metadata["otp"] ?: "false"
            ConditionField.CONTAINS_URL -> urlParser.hasUrl(event.message.orEmpty()).toString()
            ConditionField.LANGUAGE -> event.metadata["language"].orEmpty()
            ConditionField.LENGTH -> (event.message?.length ?: 0).toString()
        }
        val values = condition.values
        return when (condition.operator) {
            ConditionOperator.CONTAINS -> values.firstOrNull()?.let { left.contains(it, true) } ?: false
            ConditionOperator.CONTAINS_ANY -> values.any { left.contains(it, true) }
            ConditionOperator.CONTAINS_ALL -> values.all { left.contains(it, true) }
            ConditionOperator.EQUALS -> values.firstOrNull()?.equals(left, true) ?: false
            ConditionOperator.STARTS_WITH -> values.firstOrNull()?.let { left.startsWith(it, true) } ?: false
            ConditionOperator.ENDS_WITH -> values.firstOrNull()?.let { left.endsWith(it, true) } ?: false
            ConditionOperator.REGEX -> values.firstOrNull()?.let { Regex(it).containsMatchIn(left) } ?: false
            ConditionOperator.GREATER_THAN -> left.toDoubleOrNull()?.let { l -> values.firstOrNull()?.toDoubleOrNull()?.let { l > it } } ?: false
            ConditionOperator.LESS_THAN -> left.toDoubleOrNull()?.let { l -> values.firstOrNull()?.toDoubleOrNull()?.let { l < it } } ?: false
            ConditionOperator.BETWEEN -> {
                val min = values.getOrNull(0)?.toDoubleOrNull()
                val max = values.getOrNull(1)?.toDoubleOrNull()
                val cur = left.toDoubleOrNull()
                min != null && max != null && cur != null && cur in min..max
            }
            ConditionOperator.NOT_CONTAINS -> values.firstOrNull()?.let { !left.contains(it, true) } ?: false
        }
    }
}
