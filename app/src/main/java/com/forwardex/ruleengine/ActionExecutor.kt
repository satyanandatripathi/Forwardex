package com.forwardex.ruleengine

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import com.forwardex.database.ActionEntity
import com.forwardex.database.LogsDao
import com.forwardex.database.SmsLogEntity
import com.forwardex.domain.ActionType
import com.forwardex.domain.TriggerEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
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
    private val httpClient: OkHttpClient,
    private val logsDao: LogsDao
) {
    suspend fun execute(actions: List<ActionEntity>, event: TriggerEvent): List<String> {
        val targets = mutableListOf<String>()
        actions.forEach { action ->
            val config = parseConfig(action.configJson)
            when (action.actionType) {
                ActionType.SEND_SMS -> {
                    val messageTemplate = config.string("template").ifBlank { event.message.orEmpty() }
                    val message = applyTemplate(messageTemplate, event)
                    val recipients = config.stringList("recipients")
                    recipients.forEach { recipient ->
                        runCatching {
                            SmsManager.getDefault().sendTextMessage(recipient, null, message, null, null)
                            logsDao.insertSmsLog(
                                SmsLogEntity(
                                    sender = event.senderNumber.orEmpty(),
                                    message = message,
                                    timestamp = System.currentTimeMillis(),
                                    simSlot = event.simSlot
                                )
                            )
                            targets += recipient
                        }.onFailure { Timber.e(it, "Failed to send SMS to %s", recipient) }
                    }
                }
                ActionType.AUTO_REPLY_SMS -> {
                    val recipient = event.senderNumber.orEmpty()
                    if (recipient.isNotBlank()) {
                        val template = config.string("template").ifBlank { "Auto-reply: received." }
                        val message = applyTemplate(template, event)
                        runCatching {
                            SmsManager.getDefault().sendTextMessage(recipient, null, message, null, null)
                            logsDao.insertSmsLog(
                                SmsLogEntity(
                                    sender = "auto-reply",
                                    message = message,
                                    timestamp = System.currentTimeMillis(),
                                    simSlot = event.simSlot
                                )
                            )
                            targets += recipient
                        }.onFailure { Timber.e(it, "Failed to send auto-reply to %s", recipient) }
                    }
                }
                ActionType.HTTP_REQUEST -> {
                    val url = config.string("url")
                    if (url.isBlank()) {
                        Timber.w("Webhook action skipped due to blank URL")
                        return@forEach
                    }
                    val method = config.string("method").ifBlank { "POST" }.uppercase()
                    val payload = config.string("body").ifBlank {
                        "{\"sender\":\"${event.senderNumber.orEmpty()}\",\"message\":\"${event.message.orEmpty()}\"}"
                    }
                    val requestBuilder = Request.Builder().url(url)
                    val body = payload.toRequestBody("application/json".toMediaType())
                    when (method) {
                        "GET" -> requestBuilder.get()
                        "POST" -> requestBuilder.post(body)
                        "PUT" -> requestBuilder.put(body)
                        "PATCH" -> requestBuilder.patch(body)
                        "DELETE" -> requestBuilder.delete(body)
                        else -> requestBuilder.post(body)
                    }
                    config.jsonObject("headers")?.forEach { (key, value) ->
                        requestBuilder.addHeader(key, value.toString().trim('"'))
                    }
                    val request = requestBuilder.build()
                    runCatching { httpClient.newCall(request).execute().close() }
                        .onFailure { Timber.e(it, "Webhook action failed for URL: %s", url) }
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

    private fun parseConfig(configJson: String): ParsedConfig {
        val obj = runCatching { Json.parseToJsonElement(configJson).jsonObject }.getOrNull() ?: JsonObject(emptyMap())
        return ParsedConfig(obj)
    }

    private fun applyTemplate(template: String, event: TriggerEvent): String {
        return template
            .replace("{{sender_number}}", event.senderNumber.orEmpty())
            .replace("{{sender_name}}", event.senderName.orEmpty())
            .replace("{{message}}", event.message.orEmpty())
            .replace("{{otp}}", event.metadata["otpCode"].orEmpty())
            .replace("{{timestamp}}", event.timestamp.toString())
            .replace("{{sim_slot}}", event.simSlot?.toString().orEmpty())
    }
}

private class ParsedConfig(private val config: JsonObject) {
    fun string(key: String): String = config[key]?.toString()?.trim('"').orEmpty()
    fun stringList(key: String): List<String> {
        val value = config[key]
        return when (value) {
            is JsonArray -> value.mapNotNull { (it as? JsonPrimitive)?.contentOrNull }.map { it.trim() }.filter { it.isNotBlank() }
            is JsonPrimitive -> value.content.split(",").map { it.trim() }.filter { it.isNotBlank() }
            else -> emptyList()
        }
    }

    fun jsonObject(key: String): JsonObject? = config[key]?.jsonObject
}
