package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun googleSignIn (
    activityContext: Context,
    firebaseAuth: FirebaseAuth,
    onAuthentication: () -> Unit,
) {

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(activityContext)
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    // bottom sheet with progress bar
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(BuildConfig.googleSignInWebClientId)
        .setAutoSelectEnabled(true)
        .build()

    // todo sign-up with setFilterByAuthorizedAccounts(false)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    // todo refactor to LaunchedEffect
    coroutineScope.launch {
        try {

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext)

            handleSuccess(
                activityContext = activityContext,
                auth = firebaseAuth,
                result = result)

            // todo move downstream
            onAuthentication()

        } catch (e: Exception) {
            logException(e)
        }
    }

    // todo sign-out with clearCredentialState()
}

fun handleSuccess(
    activityContext: Context,
    auth: FirebaseAuth,
    result: GetCredentialResponse) {

    // extract credential
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {

                    // extract google id
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val googleToken = googleIdTokenCredential.idToken
                    logMessage("Google sign-in success, token: $googleToken")

                    // sign-in to firebase with google id
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activityContext as Activity) { task ->

                            if (task.isSuccessful) {
                                logMessage("Firebase sign-in success, uid: ${auth.currentUser?.uid}")

                            } else {
                                logException(task.exception)
                            }

                        }

                } catch (e: GoogleIdTokenParsingException) {
                    logException(e)
                }
            }
            else {
                logMessage("Unexpected type of credential")
            }
        }
        else -> {
            logMessage("Unexpected type of credential")
        }
    }
}

