package com.sommerengineering.baraudio.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.usersNodeId

class FirebaseDatabaseImpl {

    private val db = Firebase.database(databaseUrl)
    private lateinit var uid: String

    fun setUid(newUid: String) {
        if (::uid.isInitialized && uid == newUid) return
        uid = newUid
    }

    fun writeToken(token: String) {
        db.getReference(usersNodeId).child(uid).setValue(token)
    }
}