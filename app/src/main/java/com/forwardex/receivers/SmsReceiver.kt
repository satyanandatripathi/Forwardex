package com.forwardex.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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
class SmsReceiver : BroadcastReceiver() {
    @Inject lateinit var ruleEngine: RuleEngine
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val sms = Telephony.Sms.Intents.getMessagesFromIntent(intent).joinToString(separator = "") { it.messageBody }
        val sender = Telephony.Sms.Intents.getMessagesFromIntent(intent).firstOrNull()?.originatingAddress
        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                ruleEngine.processEvent(
                    TriggerEvent(
                        triggerType = TriggerType.SMS_RECEIVED,
                        senderNumber = sender,
                        senderName = sender,
                        message = sms
                    )
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}
