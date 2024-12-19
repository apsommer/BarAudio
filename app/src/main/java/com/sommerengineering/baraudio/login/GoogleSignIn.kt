package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.isUpdateRequired
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun signInWithGoogle (
    context: Context,
    onAuthentication: () -> Unit,
    onForceUpdate: () -> Unit) {

    // block sign-in if app update required
    if (isUpdateRequired) {
        onForceUpdate()
        return
    }

    // display ui with modal dialog
    val signInOptions = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInWebClientId)
        .build()

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(context)
    val coroutine = CoroutineScope(Dispatchers.Main)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInOptions)
        .build()

    coroutine.launch {

        // request credential
        val credential = credentialManager
            .getCredential(context, request)
            .credential

        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
            return@launch

        // extract google id
        val googleToken = GoogleIdTokenCredential
            .createFrom(credential.data)
            .idToken

        Firebase.auth
            .signInWithCredential(
                GoogleAuthProvider.getCredential(googleToken, null))
            .addOnSuccessListener { onAuthentication() }
    }
}
