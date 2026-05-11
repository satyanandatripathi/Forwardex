package com.forwardex.database

import androidx.room.TypeConverter
import com.forwardex.domain.ActionType
import com.forwardex.domain.ConditionField
import com.forwardex.domain.ConditionOperator
import com.forwardex.domain.LogicalOperator
import com.forwardex.domain.RuleStatus
import com.forwardex.domain.TriggerType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter fun fromTrigger(value: TriggerType): String = value.name
    @TypeConverter fun toTrigger(value: String): TriggerType = TriggerType.valueOf(value)
    @TypeConverter fun fromConditionField(value: ConditionField): String = value.name
    @TypeConverter fun toConditionField(value: String): ConditionField = ConditionField.valueOf(value)
    @TypeConverter fun fromConditionOp(value: ConditionOperator): String = value.name
    @TypeConverter fun toConditionOp(value: String): ConditionOperator = ConditionOperator.valueOf(value)
    @TypeConverter fun fromLogical(value: LogicalOperator): String = value.name
    @TypeConverter fun toLogical(value: String): LogicalOperator = LogicalOperator.valueOf(value)
    @TypeConverter fun fromAction(value: ActionType): String = value.name
    @TypeConverter fun toAction(value: String): ActionType = ActionType.valueOf(value)
    @TypeConverter fun fromStatus(value: RuleStatus): String = value.name
    @TypeConverter fun toStatus(value: String): RuleStatus = RuleStatus.valueOf(value)
    @TypeConverter fun fromStringList(value: List<String>): String = Json.encodeToString(ListSerializer(String.serializer()), value)
    @TypeConverter fun toStringList(value: String): List<String> =
        if (value.isEmpty()) emptyList() else Json.decodeFromString(ListSerializer(String.serializer()), value)
}
