package com.forwardex.ruleengine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import com.forwardex.database.ActionEntity
import com.forwardex.domain.ActionType
import com.forwardex.domain.TriggerEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionExecutor @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val httpClient: OkHttpClient
) {
    suspend fun execute(actions: List<ActionEntity>, event: TriggerEvent): List<String> {
        val targets = mutableListOf<String>()
        actions.forEach { action ->
            when (action.actionType) {
                ActionType.SEND_SMS -> {
                    val cfg = Json.parseToJsonElement(action.configJson).jsonObject
                    targets += cfg["recipients"]?.toString().orEmpty()
                    Timber.i("SMS action queued for %s", targets.joinToString())
                }
                ActionType.AUTO_REPLY_SMS -> Timber.i("Auto-reply queued")
                ActionType.HTTP_REQUEST -> {
                    val payload = "{\"sender\":\"${event.senderNumber.orEmpty()}\",\"message\":\"${event.message.orEmpty()}\"}"
                    val request = Request.Builder()
                        .url(Json.parseToJsonElement(action.configJson).jsonObject["url"]?.toString()?.trim('"').orEmpty())
                        .post(payload.toRequestBody("application/json".toMediaType()))
                        .build()
                    runCatching { httpClient.newCall(request).execute().close() }
                        .onFailure { Timber.e(it, "Webhook action failed") }
                }
                ActionType.NOTIFICATION -> {
                    val builder = NotificationCompat.Builder(appContext, "forwardex_engine")
                        .setContentTitle("Forwardex Rule")
                        .setContentText(event.message ?: "Event processed")
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                    NotificationManagerCompat.from(appContext).notify(action.id.toInt(), builder.build())
                }
                ActionType.CLIPBOARD -> {
                    val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val otpCode = event.metadata["otpCode"].orEmpty()
                    val value = otpCode.ifBlank { event.message.orEmpty() }
                    clipboard.setPrimaryClip(ClipData.newPlainText("otp", value))
                }
                ActionType.LOCAL_STORAGE -> Timber.i("Persisted trigger payload locally")
                ActionType.SEND_EMAIL -> Timber.i("Email queued for SMTP worker")
            }
        }
        return targets
    }
}
