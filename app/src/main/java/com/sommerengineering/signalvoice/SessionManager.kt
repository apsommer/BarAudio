package com.sommerengineering.signalvoice

import com.google.firebase.auth.FirebaseAuth
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.Session.Guest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val auth = FirebaseAuth.getInstance()

    private val _session = MutableStateFlow<Session>(Session.Guest)
    val session = _session.asStateFlow()

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

        // guest, or logout
        if (uid == null) {
            _session.value = Guest
            return
        }

        // authenticated
        _session.value = Authenticated(
            uid = uid,
            isSubscribed = false
        )

        // todo subscription check
    }
}

sealed class Session {
    object Guest : Session()
    data class Authenticated(
        val uid: String,
        val isSubscribed: Boolean
    ) : Session()
}