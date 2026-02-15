package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.MainRepository
import com.sommerengineering.baraudio.MessagesScreenRoute
import com.sommerengineering.baraudio.OnboardingTextToSpeechScreenRoute
import com.sommerengineering.baraudio.isDarkModeKey
import com.sommerengineering.baraudio.logException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: MainRepository,
    private val credentialManager: CredentialManager
) : ViewModel() {

    // onboarding
    var isOnboardingComplete by mutableStateOf(false)
        private set
    fun updateOnboarding(enabled: Boolean) {
        isOnboardingComplete = enabled
        repo.updateOnboarding(enabled)
    }
    val postLoginDestination get() =
        if (isOnboardingComplete) MessagesScreenRoute
        else OnboardingTextToSpeechScreenRoute

    // notifications
    var areNotificationsEnabled by mutableStateOf(false)
        private set
    fun updateNotificationsEnabled(enabled: Boolean) { areNotificationsEnabled = enabled }

    // dark mode
    var isDarkMode = repo.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {

        // load settings from preferences
        viewModelScope.launch {
            isOnboardingComplete = repo.loadOnboarding()
        }
    }

    fun signInWithGoogle(
        context: Context,
        onAuthentication: () -> Unit) = viewModelScope.launch {

        try {

            // launch system google sign-in dialog
            val response = credentialManager.getCredential(context, buildGoogleRequest())

            // extract google id token
            val credential = response.credential
            if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) return@launch
            val googleToken = GoogleIdTokenCredential.createFrom(credential.data).idToken

            // sign-in to firebase with google id token
            Firebase.auth
                .signInWithCredential(GoogleAuthProvider.getCredential(googleToken, null))
                .await()

            onAuthentication()

        } catch (e: Exception) {

            if (e is GetCredentialCancellationException) return@launch // user canceled dialog
            logException(e)
        }
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
}