package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.sommerengineering.baraudio.BuildConfig
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
    val googleSignInOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInClientId)
        .build()

    // todo sign-up with setFilterByAuthorizedAccounts(false)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleSignInOption)
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