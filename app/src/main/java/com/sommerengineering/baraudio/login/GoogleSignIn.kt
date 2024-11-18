package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun signInWithGoogle (
    activityContext: Context,
    onAuthentication: () -> Unit) {

    // todo use this bottom ui in production
    // display ui with bottom sheet and progress bar
//    val signInOptions = GetGoogleIdOption.Builder()
//        .setFilterByAuthorizedAccounts(true) // false to initiate sign-up flow
//        .setServerClientId(BuildConfig.googleSignInWebClientId)
//        .setAutoSelectEnabled(true)
//        .build()

    // display ui with modal dialog
    val signInOptions = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInWebClientId)
        .build()

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(activityContext)
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInOptions)
        .build()

    // todo refactor to LaunchedEffect
    coroutineScope.launch { try {

        val result = credentialManager.getCredential(
            activityContext,
            request)

        handleGoogleCredential(
            activityContext,
            result,
            onAuthentication)

        } catch (e: Exception) { logException(e) }
    }
}

fun handleGoogleCredential(
    activityContext: Context,
    result: GetCredentialResponse,
    onAuthentication: () -> Unit) {

    // extract credential
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                // extract google id
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleToken = googleIdTokenCredential.idToken

                signInWithFirebase(
                    activityContext,
                    googleToken,
                    onAuthentication)

            // do nothing
            } else { logMessage("Unexpected type of google credential") }
        } else -> { logMessage("Unexpected type of google credential") }
    }
}
