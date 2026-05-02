package com.sommerengineering.signalvoice

import com.google.firebase.auth.FirebaseAuth
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.Session.Guest
import com.sommerengineering.signalvoice.premium.EntitlementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val repo: EntitlementRepository
) {

    private val auth = FirebaseAuth.getInstance()

    private val _session = MutableStateFlow<Session>(Session.Guest)
    val session = _session.asStateFlow()

    private var entitlementJob: Job? = null

    init {

        // initialize session
        val currentUid = auth.currentUser?.uid
        onUid(currentUid)

        // handle auth state changes
        auth.addAuthStateListener { onAuth() }
    }

    val uid: String?
        get() = when (val currentSession = _session.value) {
            Guest -> null
            is Authenticated -> currentSession.uid
        }

    private fun onAuth() {

        val newUid = auth.currentUser?.uid

        // dedupe
        val currentUid = uid
        if (newUid == currentUid) return

        onUid(newUid)
    }

    private fun onUid(uid: String?) {

        entitlementJob?.cancel()

        // guest, or logout
        if (uid == null) {
            _session.value = Guest
            return
        }

        // authenticated
        _session.value = Authenticated(
            uid = uid,
            isPremium = false
        )

        // check premium entitlement
        entitlementJob = appScope.launch {

            // load entitlement from cache
            var isPremium = repo.loadPremium(uid)
            updateSession(uid, isPremium)

            // fetch entitlement from network
            isPremium = repo.fetchEntitlement(uid)
            updateSession(uid, isPremium)

            // persist entitlement
            repo.updatePremium(uid, isPremium)
        }
    }

    private fun updateSession(
        uid: String,
        isPremium: Boolean
    ) {

        val current = _session.value

        // prevent race: validate session still active, and same user
        if (current !is Authenticated) return
        if (current.uid != uid) return

        _session.value = current.copy(
            isPremium = isPremium
        )
    }
}

sealed class Session {
    object Guest : Session()
    data class Authenticated(
        val uid: String,
        val isPremium: Boolean
    ) : Session()
}