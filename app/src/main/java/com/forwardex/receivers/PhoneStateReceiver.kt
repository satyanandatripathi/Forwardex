package com.forwardex.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.forwardex.domain.TriggerEvent
import com.forwardex.domain.TriggerType
import com.forwardex.ruleengine.RuleEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PhoneStateReceiver : BroadcastReceiver() {
    @Inject lateinit var ruleEngine: RuleEngine

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val trigger = when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> TriggerType.CALL_INCOMING
            TelephonyManager.EXTRA_STATE_IDLE -> TriggerType.MISSED_CALL
            TelephonyManager.EXTRA_STATE_OFFHOOK -> TriggerType.CALL_ANSWERED
            else -> null
        } ?: return
        val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                ruleEngine.processEvent(TriggerEvent(triggerType = trigger, senderNumber = number, senderName = number, message = "call_event"))
            } finally {
                pendingResult.finish()
            }
        }
    }
}
