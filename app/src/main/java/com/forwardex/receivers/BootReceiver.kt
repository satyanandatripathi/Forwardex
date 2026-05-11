package com.forwardex.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.forwardex.services.RuleEngineForegroundService
import com.forwardex.workers.RuleRecoveryWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        ContextCompat.startForegroundService(context, Intent(context, RuleEngineForegroundService::class.java))
        val request = PeriodicWorkRequestBuilder<RuleRecoveryWorker>(6, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("rule_recovery", ExistingPeriodicWorkPolicy.UPDATE, request)
    }
}
