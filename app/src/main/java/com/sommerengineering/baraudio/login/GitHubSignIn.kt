package com.sommerengineering.baraudio.login

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.gitHubProviderId
import com.sommerengineering.baraudio.isUpdateRequired
import com.sommerengineering.baraudio.utils.logException

fun signInWithGitHub (
    context: Context,
    onAuthentication: () -> Unit,
    onForceUpdate: () -> Unit) {

    // block sign-in if app update required
    if (isUpdateRequired) {
        onForceUpdate()
        return
    }

    // launches web browser and backgrounds app
    Firebase.auth
        .startActivityForSignInWithProvider(
            context as MainActivity,
            OAuthProvider.newBuilder(gitHubProviderId).build())
        .addOnSuccessListener { onAuthentication() }
        .addOnFailureListener { logException(it) }
}