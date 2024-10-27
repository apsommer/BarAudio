package com.sommerengineering.baraudio.login

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
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
import org.koin.java.KoinJavaComponent.inject

fun signInWithGoogle (
    activityContext: Context,
    onAuthentication: () -> Unit,
) {

    // display ui with bottom sheet and progress bar
//    val signInOptions = GetGoogleIdOption.Builder()
//        .setFilterByAuthorizedAccounts(true)
//        .setServerClientId(BuildConfig.googleSignInWebClientId)
//        .setAutoSelectEnabled(true)
//        .build()

    // display ui with modal dialog
    val signInOptions = GetSignInWithGoogleOption
        .Builder(BuildConfig.googleSignInWebClientId).build()

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
                request = request,
                context = activityContext)

            handleSuccess(
                activityContext = activityContext,
                result = result)

            onAuthentication()

        } catch (e: Exception) { logException(e) }
    }
}

fun handleSuccess(
    activityContext: Context,
    result: GetCredentialResponse) {

    // inject dependencies
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)

    // extract credential
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {

                    // extract google id
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val googleToken = googleIdTokenCredential.idToken

                    logMessage(googleIdTokenCredential.profilePictureUri.toString())

                    // sign-in to firebase with google id
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)
                    firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activityContext as Activity) { task ->
                            if (task.isSuccessful) { logMessage("Firebase sign-in successful") }
                            else { logException(task.exception) }
                        }

                } catch (e: GoogleIdTokenParsingException) { logException(e) }
            } else { logMessage("Unexpected type of credential") }
        } else -> { logMessage("Unexpected type of credential") }
    }
}

// todo sign-out
