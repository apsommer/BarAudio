package com.sommerengineering.signalvoice

import com.google.firebase.auth.FirebaseAuth
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.Session.Guest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope
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
        
        entitlementJob = appScope.launch {

            // todo simulate network delay
            delay(1000)
            val isPremium = true

            val current = _session.value

            // prevent race: validate session still active, and same user
            if (current !is Authenticated) return@launch
            if (current.uid != uid) return@launch

            // update session with premium entitlement
            _session.value = current.copy(
                isPremium = isPremium
            )
        }
    }
}

sealed class Session {
    object Guest : Session()
    data class Authenticated(
        val uid: String,
        val isPremium: Boolean
    ) : Session()
}