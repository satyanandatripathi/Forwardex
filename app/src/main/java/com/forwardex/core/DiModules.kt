package com.forwardex.core

import android.content.Context
import androidx.room.Room
import com.forwardex.data.HistoryRepositoryImpl
import com.forwardex.data.RuleRepositoryImpl
import com.forwardex.data.SettingsRepositoryImpl
import com.forwardex.data.SimRepositoryImpl
import com.forwardex.database.ActionsDao
import com.forwardex.database.AnalyticsDao
import com.forwardex.database.ConditionsDao
import com.forwardex.database.ForwardexDatabase
import com.forwardex.database.HistoryDao
import com.forwardex.database.LogsDao
import com.forwardex.database.RulesDao
import com.forwardex.database.SettingsDao
import com.forwardex.database.SimProfilesDao
import com.forwardex.domain.HistoryRepository
import com.forwardex.domain.RuleRepository
import com.forwardex.domain.SettingsRepository
import com.forwardex.domain.SimRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ForwardexDatabase =
        Room.databaseBuilder(context, ForwardexDatabase::class.java, "forwardex.db").build()

    @Provides fun provideRulesDao(db: ForwardexDatabase): RulesDao = db.rulesDao()
    @Provides fun provideConditionsDao(db: ForwardexDatabase): ConditionsDao = db.conditionsDao()
    @Provides fun provideActionsDao(db: ForwardexDatabase): ActionsDao = db.actionsDao()
    @Provides fun provideHistoryDao(db: ForwardexDatabase): HistoryDao = db.historyDao()
    @Provides fun provideSettingsDao(db: ForwardexDatabase): SettingsDao = db.settingsDao()
    @Provides fun provideSimDao(db: ForwardexDatabase): SimProfilesDao = db.simProfilesDao()
    @Provides fun provideAnalyticsDao(db: ForwardexDatabase): AnalyticsDao = db.analyticsDao()
    @Provides fun provideLogsDao(db: ForwardexDatabase): LogsDao = db.logsDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun bindRules(impl: RuleRepositoryImpl): RuleRepository
    @Binds abstract fun bindHistory(impl: HistoryRepositoryImpl): HistoryRepository
    @Binds abstract fun bindSettings(impl: SettingsRepositoryImpl): SettingsRepository
    @Binds abstract fun bindSim(impl: SimRepositoryImpl): SimRepository
}
