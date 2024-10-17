package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import android.util.Log
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
import com.sommerengineering.baraudio.TAG
import com.sommerengineering.baraudio.handleException
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
                context = activityContext
            )
            handleSuccess(
                activityContext = activityContext,
                auth = firebaseAuth,
                result = result)
            onAuthentication()
        } catch (e: Exception) {
            handleException(e)
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

                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken

                    // temp
                    Log.d(TAG, "handleSuccess: $idToken")
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.id)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.givenName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.displayName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.familyName)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.phoneNumber)
                    Log.d(TAG, "handleSuccess: " + googleIdTokenCredential.profilePictureUri)

                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activityContext as Activity) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                Log.d(TAG, "handleSuccess: " + user?.displayName)
                            } else {
                                handleException(task.exception)
                            }

                        }

                } catch (e: GoogleIdTokenParsingException) {
                    handleException(e)
                }
            }
            else {
                handleException("Unexpected type of credential")
            }
        }
        else -> {
            handleException("Unexpected type of credential")
        }
    }
}

