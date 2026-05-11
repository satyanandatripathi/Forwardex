package com.forwardex.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import com.forwardex.database.SimProfileEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SimManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun currentProfiles(): List<SimProfileEntity> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return emptyList()
        }
        val manager = context.getSystemService(SubscriptionManager::class.java)
        return manager?.activeSubscriptionInfoList?.mapIndexed { index, info ->
            SimProfileEntity(
                slotIndex = info.simSlotIndex,
                carrierName = info.carrierName?.toString().orEmpty(),
                displayName = info.displayName?.toString().ifBlank { "SIM ${index + 1}" },
                subscriptionId = info.subscriptionId,
                isEsim = info.isEmbedded
            )
        }.orEmpty()
    }
}
