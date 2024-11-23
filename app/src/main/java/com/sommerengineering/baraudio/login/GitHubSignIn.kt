package com.sommerengineering.baraudio.login

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.gitHubProviderId

fun signInWithGitHub (
    context: Context,
    onAuthentication: () -> Unit) {

    // launches web browser and backgrounds app
    Firebase.auth
        .startActivityForSignInWithProvider(
            context as MainActivity,
            OAuthProvider.newBuilder(gitHubProviderId).build())
        .addOnSuccessListener { onAuthentication() }
}