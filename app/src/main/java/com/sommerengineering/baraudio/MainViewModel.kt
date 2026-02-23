package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sommerengineering.baraudio.login.GitHubAuthenticator
import com.sommerengineering.baraudio.login.GoogleAuthenticator
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.MindfulnessQuoteState
import com.sommerengineering.baraudio.uitls.MessagesScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingTextToSpeechScreenRoute
import com.sommerengineering.baraudio.uitls.RomanNumerals
import com.sommerengineering.baraudio.uitls.localOrigin
import com.sommerengineering.baraudio.uitls.queueAddDescription
import com.sommerengineering.baraudio.uitls.queueFlushDescription
import com.sommerengineering.baraudio.uitls.screenFullDescription
import com.sommerengineering.baraudio.uitls.screenWindowedDescription
import com.sommerengineering.baraudio.uitls.uiDarkDescription
import com.sommerengineering.baraudio.uitls.uiLightDescription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: MainRepository,
    private val credentialManager: CredentialManager,
    private val googleAuthenticator: GoogleAuthenticator,
    private val gitHubAuthenticator: GitHubAuthenticator,
) : ViewModel() {

    // room database
    val messages = repo.messages

    // text-to-speech
    val isTtsReady = repo.isTtsReady

    // voice
    var voices by mutableStateOf<List<Voice>>(emptyList())
        private set
    var voiceIndex by mutableStateOf(0)
        private set
    var voiceDescription by mutableStateOf("")
        private set
    fun setVoice(value: Voice) {
        repo.voice = value
        voiceIndex = voices.indexOfFirst { it.name == value.name }
        val beautifulVoice = beautifyVoiceName(value.name)
        voiceDescription = beautifulVoice
        speakMessage(beautifulVoice)
    }

    // speed
    var speed by mutableFloatStateOf(1f)
        private set
    var speedDescription by mutableStateOf("")
        private set
    fun updateSpeed(value: Float) {
        speed = value
        repo.speed = value
        speedDescription = repo.speed.toString()
    }

    // pitch
    var pitch by mutableFloatStateOf(1f)
        private set
    var pitchDescription by mutableStateOf("")
        private set
    fun updatePitch(value: Float) {
        pitch = value
        repo.pitch = value
        pitchDescription = repo.pitch.toString()
    }

    // queue behavior
    var isQueueAdd by mutableStateOf(true)
        private set
    var queueDescription by mutableStateOf("")
        private set
    fun updateQueueAdd(enabled: Boolean) {
        isQueueAdd = enabled
        repo.isQueueAdd = enabled
        val newQueueDescription =
            if (enabled) queueAddDescription
            else queueFlushDescription
        queueDescription = newQueueDescription
        speakMessage(newQueueDescription)
    }

    // mute
    var isMute by mutableStateOf(false)
        private set
    fun toggleMute() {
        isMute = !isMute
        repo.isMute = isMute
    }
    fun speakMessage(utterance: String) =
        viewModelScope.launch {
            repo.speakMessage(Message(
                timestamp = System.currentTimeMillis().toString(),
                message = utterance,
                origin = localOrigin))
        }

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

    // stream NQ
    var isNQ by mutableStateOf(true)
        private set
    fun updateNQ(enabled: Boolean) {
        isNQ = enabled
        repo.updateNQ(enabled)
    }

    // stream GC
    var isGC by mutableStateOf(true)
        private set
    fun updateGC(enabled: Boolean) {
        isGC = enabled
        repo.updateGC(enabled)
    }

    // fullscreen
    var isFullScreen by mutableStateOf(false)
        private set
    val fullScreenDescription
        get() = if (isFullScreen) screenFullDescription else screenWindowedDescription
    fun updateFullScreen(enabled: Boolean) {
        isFullScreen = enabled
        repo.updateFullScreen(enabled)
    }

    // dark mode
    var isDarkMode by mutableStateOf(false)
        private set
    val darkModeDescription
        get() = if (isDarkMode) uiDarkDescription else uiLightDescription
    fun initDarkMode(systemDefault: Boolean) =
        viewModelScope.launch { isDarkMode = repo.loadDarkMode(systemDefault) }
    fun updateDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        repo.updateDarkMode(enabled)
    }

    init {

        // load settings from preferences
        viewModelScope.launch {
            isOnboardingComplete = repo.loadOnboarding()
            isNQ = repo.loadNQ()
            isGC = repo.loadGC()
            isFullScreen = repo.loadFullScreen()
        }

        // wait for repo to finish initializing tts engine, takes a few seconds
        viewModelScope.launch {
            repo.isTtsReady.filter { it }.first()
            refreshTtsSettingsUi()
        }
    }

    private fun refreshTtsSettingsUi() {

        // add roman numerals to voice locale groups
        createBeautifulVoices()

        // voices and voice exposed by tts engine after initialization
        speed = repo.speed
        pitch = repo.pitch
        isQueueAdd = repo.isQueueAdd
        isMute = repo.isMute
        voiceDescription = beautifyVoiceName(repo.voice.name)
        speedDescription = repo.speed.toString()
        pitchDescription = repo.pitch.toString()
        queueDescription =
            if (isQueueAdd) queueAddDescription
            else queueFlushDescription
    }

    fun saveToWebhookClipboard(webhookUrl: String) = repo.saveToClipboard(webhookUrl)

    fun signOut() {
        repo.signOut()
        viewModelScope.launch {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // sign-in
    fun signInWithGoogle(
        context: Context,
        onAuthentication: () -> Unit) = viewModelScope.launch {
        if (googleAuthenticator.signIn(context)) { onAuthentication() }
    }
    fun signInWithGitHub(
        context: Context,
        onAuthentication: () -> Unit) = viewModelScope.launch {
        if (gitHubAuthenticator.signIn(context)) { onAuthentication() }
    }

    // notifications
    var areNotificationsEnabled by mutableStateOf(false)
        private set
    fun updateNotificationsEnabled(enabled: Boolean) {
        areNotificationsEnabled = enabled
    }

    // beautiful voice names
    private val beautifulVoiceNames = hashMapOf<String, String>()
    fun beautifyVoiceName(name: String) = beautifulVoiceNames[name] ?: ""
    private fun createBeautifulVoices() {

        // group voices by locale
        voices = repo.voices.sortedBy { it.locale.toLanguageTag() }
        voiceIndex = voices.indexOfFirst { it.name == repo.voice.name }

        // add roman numerals relative to locale to match system settings format
        voices
            .groupBy { it.locale.toLanguageTag() }
            .values
            .forEach { localeGroupVoices ->
                localeGroupVoices.forEachIndexed { i, voice ->
                    beautifulVoiceNames[voice.name] =
                        "${voice.locale.displayName} • Voice ${RomanNumerals.toNumeral(i)}"
                }
            }
    }
}
