package com.sommerengineering.signalvoice.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.uitls.logException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthenticator @Inject constructor(
    private val credentialManager: CredentialManager
) {

    suspend fun signIn(context: Context): Boolean {

        try {

            // bottom sheet ui
            val signInOptions = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // false to initiate sign-up flow, if needed
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build()

            // build google sign-in request
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInOptions)
                .build()

            // launch system google sign-in dialog
            val response = credentialManager.getCredential(context, request)

            // extract google id token
            val credential = response.credential
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                return false
            }
            val googleToken = GoogleIdTokenCredential.createFrom(credential.data).idToken

            // sign-in to firebase with google id token
            Firebase.auth
                .signInWithCredential(GoogleAuthProvider.getCredential(googleToken, null))
                .await()

            return true

        } catch (e: Exception) {

            // ignore user cancelled dialog
            if (e !is GetCredentialCancellationException) {
                logException(e)
            }
        }

        return false
    }
}