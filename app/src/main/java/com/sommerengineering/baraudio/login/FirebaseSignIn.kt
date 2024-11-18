package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.readFromDataStore
import com.sommerengineering.baraudio.tokenKey
import com.sommerengineering.baraudio.users

fun signInWithFirebase(
    activityContext: Context,
    googleToken: String,
    onAuthentication: () -> Unit) {

    // wrap google token into firebase credential
    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)

    try {

        Firebase.auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activityContext as Activity) { task ->

                if (task.isSuccessful) {
                    onAuthentication()
                    validateToken(activityContext)

                } else { logException(task.exception) }
            }

    } catch (e: GoogleIdTokenParsingException) { logException(e) }
}

fun validateToken(
    activityContext: Context) {

    val firebaseUser = Firebase.auth.currentUser ?: return

    firebaseUser.getIdToken(false).addOnCompleteListener { task ->

        if (task.isSuccessful) {

            // firebase user persists under many conditions, even after
            // clear clear app data, uninstall/install, and onNewToken

            // compare correct cached token with user token (potentially invalid)
            val token = task.result.token
            val cachedToken = readFromDataStore(activityContext, tokenKey)
                ?: return@addOnCompleteListener

            // update database user:token association in database, if needed
            if (token != cachedToken) { writeNewTokenToDatabase(cachedToken) }
            else { logMessage("Token already in cache, skipping database write") }
        }
    }
}

fun writeNewTokenToDatabase(token: String) {

    // get user id
    val uid = Firebase.auth.currentUser?.uid ?: return

    // write new user/token to database
    Firebase.database(databaseUrl)
        .getReference(users)
        .child(uid)
        .setValue(token)

    logMessage("New user: token written to database")
}

// todo sign-out