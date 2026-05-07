package com.sommerengineering.signalvoice.premium

import com.sommerengineering.signalvoice.PREMIUM
import com.sommerengineering.signalvoice.PreferenceStore
import com.sommerengineering.signalvoice.UID
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntitlementRepository @Inject constructor(
    private val prefs: PreferenceStore
) {

    suspend fun loadPremium(
        uid: String
    ): Boolean {

        // retrieve cache
        val storedUid = prefs.read(UID)
        val storedPremium = prefs.read(PREMIUM) ?: false

        return storedUid == uid && storedPremium
    }

    suspend fun updatePremium(
        uid: String,
        isPremium: Boolean
    ) {

        // retrieve cache
        val storedUid = prefs.read(UID)
        val storedPremium = prefs.read(PREMIUM) ?: false

        // prevent unnecessary writes
        if (uid == storedUid && isPremium == storedPremium) return

        prefs.write(UID, uid)
        prefs.write(PREMIUM, isPremium)
    }

    suspend fun fetchEntitlement(
        uid: String
    ): Boolean {

        // todo simulate network delay
        delay(1000)
        return true
    }
}