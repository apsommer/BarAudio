package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.uitls.logException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthenticator @Inject constructor(
    private val credentialManager: CredentialManager) {

    suspend fun signIn(context: Context) : Boolean {

        try {

            // launch system google sign-in dialog
            val response = credentialManager.getCredential(context, buildGoogleRequest())

            // extract google id token
            val credential = response.credential
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) { return false }
            val googleToken = GoogleIdTokenCredential.createFrom(credential.data).idToken

            // sign-in to firebase with google id token
            Firebase.auth
                .signInWithCredential(GoogleAuthProvider.getCredential(googleToken, null))
                .await()

            return true

        } catch (e: Exception) {

            // ignore user cancelled dialog
            if (e !is GetCredentialCancellationException) { logException(e) }
        }

        return false
    }

    private fun buildGoogleRequest() : GetCredentialRequest {

        // bottom sheet ui
        val signInOptions = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // false to initiate sign-up flow, if needed
            .setServerClientId(BuildConfig.googleSignInWebClientId)
            .setAutoSelectEnabled(true)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(signInOptions)
            .build()
    }

    fun signOut() {

    }
}