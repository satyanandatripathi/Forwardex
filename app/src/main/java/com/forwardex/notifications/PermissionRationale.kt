package com.forwardex.notifications

object PermissionRationale {
    val mapping = mapOf(
        "RECEIVE_SMS" to "Detect incoming messages to evaluate local automation rules.",
        "READ_SMS" to "Read SMS body required for filtering and OTP extraction.",
        "SEND_SMS" to "Send forwarded messages and auto replies.",
        "RECEIVE_MMS" to "Handle MMS message trigger events.",
        "RECEIVE_WAP_PUSH" to "Receive MMS-compatible WAP push payloads.",
        "READ_PHONE_STATE" to "Detect call lifecycle events for rules.",
        "READ_CALL_LOG" to "Resolve missed/answered/rejected call states.",
        "PROCESS_OUTGOING_CALLS" to "Optional outgoing call event triggers on supported versions.",
        "RECEIVE_BOOT_COMPLETED" to "Restore engine operation after reboot.",
        "FOREGROUND_SERVICE" to "Keep local automation engine resilient in background.",
        "POST_NOTIFICATIONS" to "Show engine status and action feedback.",
        "READ_CONTACTS" to "Match sender against contact groups.",
        "CALL_PHONE" to "Optional call action execution by user-configured rules."
    )
}
