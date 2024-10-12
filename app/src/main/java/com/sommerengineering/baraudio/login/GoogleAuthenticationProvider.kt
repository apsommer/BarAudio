package com.sommerengineering.baraudio.login

import android.content.Context
import android.credentials.GetCredentialException
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun googleSignIn (
    activityContext: Context
) {

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(activityContext)
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    // consider google accounts on device
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true) // filter by accounts already used to sign-in
        .setAutoSelectEnabled(true) // automatic sign-in for users who register with their single account
        .build()

    // todo sign-up with setFilterByAuthorizedAccounts(false)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            handleSuccess(result)
        } catch (exception: Exception) {
            handleFailure(exception)
        }
    }

    // todo sign-out with clearCredentialState()
}

fun handleSuccess(result: GetCredentialResponse) {

}

fun handleFailure(exception: Exception) {

}