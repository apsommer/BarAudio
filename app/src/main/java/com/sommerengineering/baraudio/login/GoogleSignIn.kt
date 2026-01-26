package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.isUpdateRequired
import com.sommerengineering.baraudio.utils.logException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun signInWithGoogle (
    context: Context,
    credentialManager: CredentialManager,
    onAuthentication: () -> Unit,
    onForceUpdate: () -> Unit) {

    // block sign-in if app update required
    if (isUpdateRequired) {
        onForceUpdate()
        return
    }

    // display ui with bottom sheet
    val signInOptions = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false) // false to initiate sign-up flow, if needed
        .setServerClientId(BuildConfig.googleSignInWebClientId)
        .setAutoSelectEnabled(true)
        .build()

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInOptions)
        .build()

    // request credential
    CoroutineScope(Dispatchers.Main).launch {
        try {
            handleSuccess(
                response = credentialManager.getCredential(context, request),
                onAuthentication = onAuthentication)

        } catch (e: GetCredentialException) {
            handleFailure(e)
        }
    }
}

fun handleSuccess(
    response: GetCredentialResponse,
    onAuthentication: () -> Unit) {

    // extract credential
    val credential = response.credential
    if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return

    // extract google id
    val googleToken = GoogleIdTokenCredential
        .createFrom(credential.data)
        .idToken

    Firebase.auth
        .signInWithCredential(
            GoogleAuthProvider.getCredential(googleToken, null))
        .addOnSuccessListener { onAuthentication() }
        .addOnFailureListener { logException(it) }
}

fun handleFailure(
    e: GetCredentialException) {

    // user canceled sign-in dialog, do not send to crashlytics
    if (e is GetCredentialCancellationException) return

    logException(e)
}