package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.MindfulnessQuoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext val context: Context,
    val repo: MainRepository,
    val credentialManager: CredentialManager, // todo remove
) : ViewModel() {

    // database
    val messages = repo.messages
    fun startListeningToDatabase() = repo.startListeningToDatabase()
    fun deleteAllMessages() = repo.deleteAllMessages()
    fun deleteMessage(message: Message) = repo.deleteMessage(message)
    override fun onCleared() { repo.stopListening() }

    // text-to-speech
    val isTtsInit = repo.isTtsInit

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
        voiceDescription = beautifyVoiceName(value.name)
        speakLastMessage()
    }
    private val beautifulVoiceNames = hashMapOf<String, String>()

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
        queueDescription =
            if (enabled) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }

    // mute
    var isMute by mutableStateOf(false)
        private set
    fun toggleMute() {
        isMute = !isMute
        repo.isMute = isMute
    }
    fun speakLastMessage() {
        val lastMessage =
            if (messages.value.isEmpty()) defaultMessage
            else messages.value.first().message
        repo.speakMessage(lastMessage)
    }

    // mindfulness quote
    private var _mindfulnessQuoteState: MutableStateFlow<MindfulnessQuoteState> = MutableStateFlow(MindfulnessQuoteState.Idle)
    val mindfulnessQuoteState = _mindfulnessQuoteState.asStateFlow()
    var isShowQuote by mutableStateOf(true)
        private set
    fun updateShowQuote(enabled: Boolean) {
        isShowQuote = enabled
        repo.updateShowQuote(enabled)
    }
    fun initShowQuote() =
        viewModelScope.launch { isShowQuote = repo.loadShowQuote() }

    // futures webhooks
    var isFuturesWebhooks by mutableStateOf(true)
        private set
    fun initFuturesWebhooks() =
        viewModelScope.launch { isFuturesWebhooks = repo.loadFuturesWebhooks() }
    fun updateFuturesWebhooks(enabled: Boolean) {
        isFuturesWebhooks = enabled
        repo.updateFuturesWebhooks(enabled)
    }

    // fullscreen
    var isFullScreen by mutableStateOf(false)
        private set
    val fullScreenDescription
        get() = if (isFullScreen) screenFullDescription else screenWindowedDescription
    fun initFullScreen() =
        viewModelScope.launch { isFullScreen = repo.loadFullScreen() }
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

        // wait for system initialization of tts engine, takes a few seconds
        viewModelScope.launch(Dispatchers.Default) {
            repo.isTtsReady.filter { it }.first()
            refreshTtsSettingsUi()
        }

        // request mindfulness quote from network
        viewModelScope.launch(Dispatchers.IO) {
            _mindfulnessQuoteState.update { MindfulnessQuoteState.Loading }
            try { _mindfulnessQuoteState.update { MindfulnessQuoteState.Success(repo.getMindfulnessQuote()) }}
            catch (e: Exception) { _mindfulnessQuoteState.update { MindfulnessQuoteState.Error(e.message) }}
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
            if (isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }

    fun saveToWebhookClipboard(webhookUrl: String) = repo.saveToClipboard(webhookUrl)

    private fun createBeautifulVoices() {

        // group voices by locale
        voices = repo.voices.sortedBy { it.locale.toLanguageTag() }
        voiceIndex = voices.indexOfFirst { it.name == repo.voice.name }

        val groupedByLocaleVoices = voices.groupBy { it.locale.toLanguageTag() }

        // add roman numeral to name
        groupedByLocaleVoices.keys
            .forEach { localeGroup ->
                val localeVoices = groupedByLocaleVoices[localeGroup] ?: return@forEach
                localeVoices.forEachIndexed { i, voice ->
                    beautifulVoiceNames[voice.name] = enumerateVoices(voice, i)
                }
            }
    }

    private fun enumerateVoices(
        voice: Voice,
        number: Int): String {

        val displayName = voice.locale.displayName
        var romanNumeral = "I"

        if (number == 1) romanNumeral = "II"
        if (number == 2) romanNumeral = "III"
        if (number == 3) romanNumeral = "IV"
        if (number == 4) romanNumeral = "V"
        if (number == 5) romanNumeral = "VI"
        if (number == 6) romanNumeral = "VII"
        if (number == 7) romanNumeral = "VIII"
        if (number == 8) romanNumeral = "IX"
        if (number == 9) romanNumeral = "X"
        if (number == 10) romanNumeral = "XI"
        if (number == 11) romanNumeral = "XII"
        if (number == 12) romanNumeral = "XIII"
        if (number == 13) romanNumeral = "XIV"
        if (number == 14) romanNumeral = "XV"
        if (number == 15) romanNumeral = "XVI"
        if (number == 16) romanNumeral = "XVII"
        if (number == 17) romanNumeral = "XVIII"
        if (number == 18) romanNumeral = "XIX"
        if (number == 19) romanNumeral = "XX"

        return "$displayName • Voice $romanNumeral"
    }

    fun beautifyVoiceName(name: String) = beautifulVoiceNames[name] ?: ""
}
