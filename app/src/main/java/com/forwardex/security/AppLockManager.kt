package com.forwardex.security

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLockManager @Inject constructor() {
    fun promptBiometric(
        activity: FragmentActivity,
        promptInfo: BiometricPrompt.PromptInfo,
        callback: BiometricPrompt.AuthenticationCallback
    ) {
        val prompt = BiometricPrompt(activity, activity.mainExecutor, callback)
        prompt.authenticate(promptInfo)
    }
}
