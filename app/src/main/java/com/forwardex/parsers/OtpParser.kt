package com.forwardex.parsers

import com.forwardex.domain.OtpExtraction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpParser @Inject constructor() {
    private val otpRegexes = listOf(
        Regex("\\b(\\d{4,8})\\b"),
        Regex("(?:otp|code|verification|auth)[^0-9]{0,20}(\\d{4,8})", RegexOption.IGNORE_CASE)
    )

    fun extract(message: String, sender: String?): OtpExtraction? {
        val lower = message.lowercase()
        val hasOtpSignal = listOf("otp", "verification", "auth", "code", "password").any { lower.contains(it) }
        if (!hasOtpSignal && !Regex("\\b\\d{4,8}\\b").containsMatchIn(message)) return null

        val code = otpRegexes.firstNotNullOfOrNull { it.find(message)?.groupValues?.lastOrNull() } ?: return null
        val category = when {
            lower.contains("bank") || lower.contains("account") -> "banking"
            lower.contains("gov") || lower.contains("uid") -> "government"
            lower.contains("shop") || lower.contains("order") -> "shopping"
            else -> "social"
        }
        val expiry = Regex("(\\d{1,2})\\s?(min|minute)", RegexOption.IGNORE_CASE)
            .find(message)?.groupValues?.drop(1)?.joinToString(" ")

        return OtpExtraction(code = code, sender = sender, expiry = expiry, appName = sender, category = category)
    }
}
