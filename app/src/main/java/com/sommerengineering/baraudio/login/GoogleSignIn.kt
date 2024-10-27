package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
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

    // bottom sheet with progress bar
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(BuildConfig.googleSignInWebClientId)
        .setAutoSelectEnabled(true)
        .build()

    getGoogleCredential(
        activityContext,
        firebaseAuth,
        onAuthentication,
        googleIdOption)

    // todo sign-out with clearCredentialState()
}

fun signUpWithGoogle(
    activityContext: Context,
    firebaseAuth: FirebaseAuth,
    onAuthentication: () -> Unit) {

    val signInWithGoogleOption = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInWebClientId).build()

    getGoogleCredential(
        activityContext,
        firebaseAuth,
        onAuthentication,
        signInWithGoogleOption)
}

fun getGoogleCredential(
    activityContext: Context,
    firebaseAuth: FirebaseAuth,
    onAuthentication: () -> Unit,
    credentialOption: CredentialOption) {

    // create credential manager and coroutine
    val credentialManager = CredentialManager.create(activityContext)
    val coroutineScope = CoroutineScope(Dispatchers.Main)

    // create request
    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(credentialOption)
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

            // todo move downstream to handleSuccess
            onAuthentication()
        }
        catch (e: NoCredentialException) {
            signUpWithGoogle(
                activityContext,
                firebaseAuth,
                onAuthentication)
        }
        catch (e: Exception) { logException(e) }
    }
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

                    // sign-in to firebase with google id
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activityContext as Activity) { task ->
                            if (task.isSuccessful) { logMessage("Sign-in with firebase") }
                            else { logException(task.exception) }
                        }
                    
                } catch (e: GoogleIdTokenParsingException) { logException(e) }
            } else { logMessage("Unexpected type of credential") }
        } else -> { logMessage("Unexpected type of credential") }
    }
}

