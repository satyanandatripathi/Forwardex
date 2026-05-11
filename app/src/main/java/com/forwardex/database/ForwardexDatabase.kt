package com.forwardex.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RuleEntity::class,
        ConditionEntity::class,
        ActionEntity::class,
        HistoryEntity::class,
        SmsLogEntity::class,
        CallLogEntity::class,
        SettingsEntity::class,
        SimProfileEntity::class,
        AnalyticsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ForwardexDatabase : RoomDatabase() {
    abstract fun rulesDao(): RulesDao
    abstract fun conditionsDao(): ConditionsDao
    abstract fun actionsDao(): ActionsDao
    abstract fun historyDao(): HistoryDao
    abstract fun settingsDao(): SettingsDao
    abstract fun simProfilesDao(): SimProfilesDao
    abstract fun analyticsDao(): AnalyticsDao
    abstract fun logsDao(): LogsDao
}
