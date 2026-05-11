package com.forwardex

import com.google.common.truth.Truth.assertThat
import com.forwardex.database.ConditionEntity
import com.forwardex.domain.ConditionField
import com.forwardex.domain.ConditionOperator
import com.forwardex.domain.LogicalOperator
import com.forwardex.domain.TriggerEvent
import com.forwardex.domain.TriggerType
import com.forwardex.parsers.UrlParser
import com.forwardex.ruleengine.ConditionEvaluator
import org.junit.Test

class ConditionEvaluatorTest {
    private val evaluator = ConditionEvaluator(UrlParser())

    @Test
    fun evaluatesContainsAndRegex() {
        val event = TriggerEvent(
            triggerType = TriggerType.SMS_RECEIVED,
            senderNumber = "+911234567890",
            message = "Code 9988 at https://site.com"
        )
        val conditions = listOf(
            ConditionEntity(ruleId = 1, field = ConditionField.SENDER_NUMBER, operator = ConditionOperator.CONTAINS, values = listOf("+91")),
            ConditionEntity(ruleId = 1, field = ConditionField.MESSAGE_CONTENT, operator = ConditionOperator.REGEX, values = listOf(".*\\d{4}.*"), logicalOperator = LogicalOperator.AND),
            ConditionEntity(ruleId = 1, field = ConditionField.CONTAINS_URL, operator = ConditionOperator.EQUALS, values = listOf("true"), logicalOperator = LogicalOperator.AND)
        )

        assertThat(evaluator.evaluate(conditions, event)).isTrue()
    }
}
