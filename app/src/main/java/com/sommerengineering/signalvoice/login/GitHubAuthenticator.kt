package com.sommerengineering.signalvoice.login

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.signalvoice.MainActivity
import com.sommerengineering.signalvoice.uitls.gitHubProviderId
import com.sommerengineering.signalvoice.uitls.logException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GitHubAuthenticator @Inject constructor() {

    suspend fun signIn(
        context: Context
    ): Boolean {

        try {

            // launches web browser and backgrounds app
            Firebase.auth
                .startActivityForSignInWithProvider(
                    context as MainActivity,
                    OAuthProvider.newBuilder(gitHubProviderId).build()
                )
                .await()

            return true

        } catch (e: Exception) {
            logException(e)
        }

        return false
    }
}