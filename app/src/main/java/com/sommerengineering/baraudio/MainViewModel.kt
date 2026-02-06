package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.messages.MindfulnessQuoteState
import com.sommerengineering.baraudio.messages.tradingviewWhitelistIps
import com.sommerengineering.baraudio.messages.trendspiderWhitelistIp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repo: MainRepository,
    val tts: TextToSpeechImpl,
    val credentialManager: CredentialManager,
    @ApplicationContext val context: Context
) : ViewModel() {

    val voices = repo.voices
    val messages = repo.messages
    val voiceDescription = repo.voiceDescription
    var speedDescription = repo.speedDescription
    var pitchDescription = repo.pitchDescription
    var queueDescription = repo.queueDescription
    var isMute = repo.isMute
    var isShowQuote = repo.isShowQuote
    var isFuturesWebhooks = repo.isFuturesWebhooks

    private var _mindfulnessQuoteState: MutableStateFlow<MindfulnessQuoteState> = MutableStateFlow(MindfulnessQuoteState.Idle)
    val mindfulnessQuoteState = _mindfulnessQuoteState.asStateFlow()

    // fullscreen
    val isFullScreen = repo.isFullScreen
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    var fullScreenDescription = isFullScreen
        .map { if (it) screenFullDescription else screenWindowedDescription }
        .stateIn(viewModelScope, SharingStarted.Eagerly, screenWindowedDescription)

    fun setIsFullScreen(enabled: Boolean) =
        viewModelScope.launch { repo.setFullScreen(enabled) }

    init {

        // wait for system initialization of tts engine, takes a few seconds
        viewModelScope.launch(Dispatchers.Main) {
            repo.observeTtsInit { repo.initTtsSettings() }.collect()
        }

        // request mindfulness quote from network
        viewModelScope.launch(Dispatchers.IO) {
            _mindfulnessQuoteState.value = MindfulnessQuoteState.Loading
            try { _mindfulnessQuoteState.value = MindfulnessQuoteState.Success(repo.getMindfulnessQuote()) }
            catch (e: Exception) { _mindfulnessQuoteState.value = MindfulnessQuoteState.Error(e.message) }
        }
    }

    fun setVoice(voice: Voice) = repo.setVoice(voice)
    fun beautifyVoiceName(name: String) = repo.beautifyVoiceName(name)
    fun getVoiceIndex() = repo.getVoiceIndex()
    fun getSpeed() = repo.getSpeed()
    fun setSpeed(rawSpeed: Float) { repo.setSpeed(rawSpeed) }
    fun getPitch() = repo.getPitch()
    fun setPitch(rawPitch: Float) = repo.setPitch(rawPitch)
    fun isQueueAdd() = repo.isQueueAdd()
    fun setIsQueueAdd(isChecked: Boolean) = repo.setIsQueueAdd(isChecked)
    fun toggleMute() = repo.toggleMute()
    fun speakLastMessage() = repo.speakLastMessage()
    fun saveToWebhookClipboard(webhookUrl: String) = repo.saveToClipboard(webhookUrl)
    fun showQuote(isChecked: Boolean) = repo.showQuote(isChecked)
    fun setFuturesWebhooks(isChecked: Boolean) = repo.setFuturesWebhooks(isChecked)

    // dark mode
    var isDarkMode by mutableStateOf(true)
    var isSystemInDarkTheme = false
    var uiModeDescription by mutableStateOf("")

    fun setUiMode() {

        isDarkMode =
            if (Firebase.auth.currentUser == null) isSystemInDarkTheme
            else readFromDataStore(context, isDarkModeKey)?.toBooleanStrictOrNull() ?: true

        uiModeDescription =
            if (isDarkMode) uiModeDarkDescription
            else uiModeLightDescription
    }

    fun setIsDarkMode(
        context: Context,
        isChecked: Boolean) {

        isDarkMode = isChecked
        writeToDataStore(context, isDarkModeKey, isChecked.toString())

        uiModeDescription =
            if (isDarkMode) uiModeDarkDescription
            else uiModeLightDescription
    }


















    // images //////////////////////////////////////////////////////////////////////////////////////

    fun getGitHubImageId() =
        if (isDarkMode) R.drawable.github_light
        else R.drawable.github_dark

    fun getBackgroundId() =
        if (isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

    fun getOriginImageId(
        origin: String): Int? {

        return when (origin) {
            in tradingviewWhitelistIps -> {
                if (isDarkMode) R.drawable.tradingview_light
                else R.drawable.tradingview_dark
            }
            trendspiderWhitelistIp -> R.drawable.trendspider
            com.sommerengineering.baraudio.messages.error -> R.drawable.error
            else -> {
                if (BuildConfig.BUILD_TYPE == buildTypeDebug) R.drawable.insomnia
                else null
            }
        }
    }
}
