package com.sommerengineering.signalvoice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sommerengineering.signalvoice.login.GitHubAuthenticator
import com.sommerengineering.signalvoice.login.GoogleAuthenticator
import com.sommerengineering.signalvoice.messages.FeedMode
import com.sommerengineering.signalvoice.onboarding.webhook.VerificationState.RECEIVED
import com.sommerengineering.signalvoice.onboarding.webhook.VerificationState.WAITING
import com.sommerengineering.signalvoice.onboarding.webhook.VerificationUiState
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.uitls.RomanNumerals
import com.sommerengineering.signalvoice.uitls.screenFullDescription
import com.sommerengineering.signalvoice.uitls.screenWindowedDescription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: MainRepository,
    private val credentialManager: CredentialManager,
    private val googleAuthenticator: GoogleAuthenticator,
    private val gitHubAuthenticator: GitHubAuthenticator,
) : ViewModel() {

    // room database
    val messages = repo.messages.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    // voice
    var voices by mutableStateOf<List<Voice>>(emptyList())
        private set
    private val beautifulVoiceNames = hashMapOf<String, String>()
    var voiceIndex by mutableStateOf(0)
        private set
    var voiceDescription by mutableStateOf("")
        private set

    fun setVoice(value: Voice) {
        repo.voice = value
        voiceIndex = voices.indexOfFirst { it.name == value.name }
        val beautifulVoice = beautifyVoiceName(value.name)
        voiceDescription = beautifulVoice
        speakUtterance(beautifulVoice)
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

    // mute
    val isMute = repo.isMute
    fun toggleMute() = repo.setMute(!isMute.value)

    fun speakUtterance(utterance: String) =
        viewModelScope.launch {
            repo.speakMessage(
                Message(
                    timestamp = System.currentTimeMillis().toString(),
                    message = utterance, null, null
                )
            )
        }

    fun speakMessage(message: Message) =
        viewModelScope.launch { repo.speakMessage(message) }

    // onboarding
    var isOnboardingComplete by mutableStateOf(false)
        private set

    fun updateOnboarding(enabled: Boolean) {
        isOnboardingComplete = enabled
        repo.updateOnboarding(enabled)
    }

    var isEmptyState by mutableStateOf(true)
        private set

    fun updateEmptyState(enabled: Boolean) {
        isEmptyState = enabled
        repo.updateEmptyState(enabled)
    }

    // stream ZN
    var isZN by mutableStateOf(true)
        private set

    fun updateZN(enabled: Boolean) {
        isZN = enabled
        repo.updateZN(enabled)
    }

    // stream NQ
    var isNQ by mutableStateOf(true)
        private set

    fun updateNQ(enabled: Boolean) {
        isNQ = enabled
        repo.updateNQ(enabled)
    }

    // stream ES
    var isES by mutableStateOf(true)
        private set

    fun updateES(enabled: Boolean) {
        isES = enabled
        repo.updateES(enabled)
    }

    // stream BTC
    var isBTC by mutableStateOf(true)
        private set

    fun updateBTC(enabled: Boolean) {
        isBTC = enabled
        repo.updateBTC(enabled)
    }

    // stream GC
    var isGC by mutableStateOf(true)
        private set

    fun updateGC(enabled: Boolean) {
        isGC = enabled
        repo.updateGC(enabled)
    }

    // stream SI
    var isSI by mutableStateOf(true)
        private set

    fun updateSI(enabled: Boolean) {
        isSI = enabled
        repo.updateSI(enabled)
    }

    // feed mode: linear or grouped
    var feedMode by mutableStateOf(FeedMode.Linear)
        private set

    fun toggleFeedMode() {
        val newFeedMode = if (feedMode == FeedMode.Linear) FeedMode.Grouped else FeedMode.Linear
        feedMode = newFeedMode
        repo.updateFeedMode(newFeedMode)
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

    private fun refreshTtsSettingsUi() {

        // add roman numerals to voice locale groups
        createBeautifulVoices()

        // voices and voice exposed by tts engine after initialization
        speed = repo.speed
        pitch = repo.pitch
        voiceDescription = beautifyVoiceName(repo.voice.name)
        speedDescription = repo.speed.toString()
        pitchDescription = repo.pitch.toString()
    }

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
        onAuthentication: () -> Unit
    ) = viewModelScope.launch {
        if (googleAuthenticator.signIn(context)) {
            onAuthentication()
        }
    }

    fun signInWithGitHub(
        context: Context,
        onAuthentication: () -> Unit
    ) = viewModelScope.launch {
        if (gitHubAuthenticator.signIn(context)) {
            onAuthentication()
        }
    }

    // notifications
    var hasRequestedNotificationPermission by mutableStateOf(false)
        private set
    var areNotificationsEnabled by mutableStateOf(false)
        private set
    private val _notificationPermissionResult = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )
    val notificationPermissionResult = _notificationPermissionResult.asSharedFlow()

    fun onNotificationPermissionResult(enabled: Boolean) {
        hasRequestedNotificationPermission = true
        areNotificationsEnabled = enabled
        _notificationPermissionResult.tryEmit(Unit)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        areNotificationsEnabled = enabled
    }

    // beautiful voice names
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

    // copy webhook
    val webhookUrl get() = repo.webhookUrl
    fun copyWebhook(
        context: Context
    ) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)
    }

    // onboarding: setup webhook, verify user signal received
    private var isVerifiedLocked = false
    private var verificationStartTime: Long? = null
    fun setVerificationStartTime() {
        verificationStartTime = System.currentTimeMillis()
        isVerifiedLocked = false
    }

    val verificationUiState = messages.map { messages ->
        val startTime = verificationStartTime
        val latestMessage =
            startTime?.let {
                messages.firstOrNull {
                    it.source != null &&
                            it.timestamp.toLong() > startTime
                }
            }
        if (latestMessage != null) {
            isVerifiedLocked = true
            VerificationUiState(RECEIVED, latestMessage.message)
        } else {
            VerificationUiState(WAITING)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        VerificationUiState(WAITING)
    )

    ////////////////////////////////////////////////////////////////////////////////////////////////

    init {

        // load settings from preferences
        // block main thread is acceptable for datastore read ~3 ms each
        runBlocking {
            isOnboardingComplete = repo.loadOnboarding()
            isEmptyState = repo.loadEmptyState()
            isZN = repo.loadZN()
            isNQ = repo.loadNQ()
            isBTC = repo.loadBTC()
            isES = repo.loadES()
            isGC = repo.loadGC()
            isSI = repo.loadSI()
            feedMode = repo.loadFeedMode()
            isFullScreen = repo.loadFullScreen()
        }

        // wait for repo to finish initializing tts engine, takes a few seconds
        viewModelScope.launch {
            repo.isTtsReady.filter { it }.first()
            refreshTtsSettingsUi()
        }

        // dismiss empty state card on first user signal
        viewModelScope.launch {
            messages.collect { messages ->
                val hasUserSignal = messages.any { it.source != null }
                if (hasUserSignal && isEmptyState) {
                    updateEmptyState(false)
                }
            }
        }

        // cold start hydration sync of firebase to local room database
        viewModelScope.launch {
            repo.hydrateMessages()
        }
    }
}
