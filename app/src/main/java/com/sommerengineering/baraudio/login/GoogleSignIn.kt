package com.sommerengineering.baraudio.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.TAG
import com.sommerengineering.baraudio.handleException
import com.sommerengineering.baraudio.logMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun googleSignIn (
    activityContext: Context,
    onAuthentication: () -> Unit,
) {

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(activityContext)
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    // modal dialog
    val googleSignInOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInWebClientId)
        .build()

    // bottom sheet with progress bar
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(BuildConfig.googleSignInWebClientId)
        .setAutoSelectEnabled(true)
        .build()

    // todo sign-up with setFilterByAuthorizedAccounts(false)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleSignInOption) // modal dialog
//        .addCredentialOption(googleIdOption) // bottom sheet with progress bar
        .build()

    // todo refactor to LaunchedEffect
    coroutineScope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            handleSuccess(result)
            onAuthentication()
        } catch (e: Exception) {
            handleException(e)
        }
    }

    // todo sign-out with clearCredentialState()
}

fun handleSuccess(result: GetCredentialResponse) {

    // extract credential
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {

                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.id)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.idToken)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.givenName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.displayName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.familyName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.phoneNumber)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.profilePictureUri)

                } catch (e: GoogleIdTokenParsingException) { handleException(e) }
            }
            else { logMessage("Unexpected type of credential") }
        }
        else -> { logMessage("Unexpected type of credential") }
    }
}

