package com.forwardex.analytics

import com.forwardex.database.AnalyticsDao
import com.forwardex.database.AnalyticsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardAnalytics @Inject constructor(
    private val analyticsDao: AnalyticsDao
) {
    fun observeMetrics(): Flow<List<AnalyticsEntity>> = analyticsDao.observeAll()

    suspend fun track(metric: String, value: Long, day: String) {
        analyticsDao.insert(AnalyticsEntity(metric = metric, value = value, day = day))
    }
}
