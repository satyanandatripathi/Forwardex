package com.forwardex.parsers

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlParser @Inject constructor() {
    private val urlRegex = Regex("https?://[\\w./?=&%-]+", RegexOption.IGNORE_CASE)
    fun hasUrl(content: String): Boolean = urlRegex.containsMatchIn(content)
    fun extractAll(content: String): List<String> = urlRegex.findAll(content).map { it.value }.toList()
}

@Singleton
class SenderNormalizer @Inject constructor() {
    fun normalize(sender: String?): String = sender?.replace(" ", "")?.replace("-", "")?.trim()?.lowercase().orEmpty()
}
