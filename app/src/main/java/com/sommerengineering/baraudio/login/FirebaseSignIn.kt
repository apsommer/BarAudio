package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.dataStore
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.tokenKey
import com.sommerengineering.baraudio.writeNewUserToDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun signInWithFirebase(
    activityContext: Context,
    googleToken: String) {

    // wrap google token into firebase credential
    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)

    try {

        Firebase.auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activityContext as Activity) { task ->
                if (task.isSuccessful) { handleSuccess(activityContext) }
                else { logException(task.exception) }
            }

    } catch (e: GoogleIdTokenParsingException) { logException(e) }
}

fun handleSuccess(activityContext: Context) {

    logMessage("Firebase sign-in successful")

    val firebaseUser = Firebase.auth.currentUser ?: return
    firebaseUser.getIdToken(false).addOnCompleteListener { task ->

        if (task.isSuccessful) {

            // todo somehow firebase user (and its token) persists after clear clear app data, uninstall/install, and onNewToken callback, wow!
            val token = task.result.token
            val cachedToken = runBlocking {
                activityContext.dataStore.data.map { it[tokenKey] }.first()
            } ?: return@addOnCompleteListener

            // compare cached token with user token
            if (token != cachedToken) { writeNewUserToDatabase(cachedToken) }
            else { logMessage("Token already in cache, skipping database write") }
        }
    }
}

// todo sign-out