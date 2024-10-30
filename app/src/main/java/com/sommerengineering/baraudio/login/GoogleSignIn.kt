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
import com.sommerengineering.baraudio.dataStore
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.tokenKey
import com.sommerengineering.baraudio.writeNewUserToDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.java.KoinJavaComponent.inject

fun signInWithGoogle (
    activityContext: Context,
    onAuthentication: () -> Unit,
) {

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
                request = request,
                context = activityContext)

            handleGoogleCredential(
                activityContext = activityContext,
                result = result)

            onAuthentication()

        } catch (e: Exception) { logException(e) }
    }
}

fun handleGoogleCredential(
    activityContext: Context,
    result: GetCredentialResponse) {

    // extract credential
    val credential = result.credential

    when (credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                // extract google id
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleToken = googleIdTokenCredential.idToken

                // todo capture name, photoUrl, etc. and populate topBar

                logMessage("Google sign-in successful")
                signInWithFirebase(activityContext, googleToken)

            } else { logMessage("Unexpected type of google credential") }
        } else -> { logMessage("Unexpected type of google credential") }
    }
}

fun signInWithFirebase(
    activityContext: Context,
    googleToken: String) {

    // inject dependencies
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)

    // wrap google token into firebase credential
    val firebaseCredential = GoogleAuthProvider.getCredential(googleToken, null)

    try {

        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(activityContext as Activity) { task ->
                if (task.isSuccessful) { handleSuccess(activityContext) }
                else { logException(task.exception) }
            }

    } catch (e: GoogleIdTokenParsingException) { logException(e) }
}

fun handleSuccess(activityContext: Context) {

    logMessage("Firebase sign-in successful")

    // get user id
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
    val firebaseUser = firebaseAuth.currentUser ?: return

    firebaseUser.getIdToken(false).addOnCompleteListener { task ->

        if (task.isSuccessful) {

            val token = task.result.token // todo somehow this token can persist past clear app data, uninstall/install, and onNewToken callback, wow!
            val cachedToken =
                runBlocking {
                    activityContext.dataStore.data.map { it[tokenKey] }.first()
                } ?: return@addOnCompleteListener

            // compare cached token with user token
            if (token != cachedToken) { writeNewUserToDatabase(cachedToken) }
            else { logMessage("Token already in cache, skipping database write") }
        }
    }
}

// todo sign-out
