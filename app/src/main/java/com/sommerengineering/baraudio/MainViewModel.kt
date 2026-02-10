package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
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
    fun startListening() = repo.startListening()
    fun deleteAllMessages() = repo.deleteAllMessages()
    fun deleteMessage(message: Message) = repo.deleteMessage(message)
    override fun onCleared() { repo.stopListening() }

    // text-to-speech
    val isTtsInit = repo.isTtsInit

    // voice
    var voices by mutableStateOf<List<Voice>>(emptyList())
    private val beautifulVoiceNames = hashMapOf<String, String>()
    var voiceIndex = 0
    var voice
        get() = repo.voice
        set(value) {
            repo.voice = value
            voiceDescription = beautifyVoiceName(value.name)
            speakLastMessage()
        }
    var voiceDescription by mutableStateOf("")

    // speed
    var speed
        get() = repo.speed
        set(value) {
            repo.speed = value
            speedDescription = repo.speed.toString()
        }
    var speedDescription by mutableStateOf("")

    // pitch
    var pitch
        get() = repo.pitch
        set(value) {
            repo.pitch = value
            pitchDescription = repo.pitch.toString()
        }
    var pitchDescription by mutableStateOf("")

    // queue behavior
    var isQueueAdd
        get() = repo.isQueueAdd
        set(value) {
            repo.isQueueAdd = value
            queueDescription =
                if (value) queueBehaviorAddDescription
                else queueBehaviorFlushDescription
        }
    var queueDescription by mutableStateOf("")

    // mute button
    var isMute = repo.isMute
    fun toggleMute() = repo.toggleMute()
    fun speakLastMessage() {
        val lastMessage =
            if (messages.value.isEmpty()) defaultMessage
            else messages.value.first().message
        repo.speakMessage(lastMessage)
    }

    // mindfulness quote
    var isShowQuote = repo.isShowQuote
    fun setIsShowQuote(isChecked: Boolean) = repo.showQuote(isChecked)
    private var _mindfulnessQuoteState: MutableStateFlow<MindfulnessQuoteState> = MutableStateFlow(MindfulnessQuoteState.Idle)
    val mindfulnessQuoteState = _mindfulnessQuoteState.asStateFlow()

    // futures webhooks
    var isFuturesWebhooks = repo.isFuturesWebhooks
    fun setFuturesWebhooks(isChecked: Boolean) = repo.setFuturesWebhooks(isChecked)

    // fullscreen
    var isFullScreen by mutableStateOf(false)
        private set
    val fullScreenDescription
        get() = if (isFullScreen) screenFullDescription else screenWindowedDescription
    fun initFullScreen() =
        viewModelScope.launch { isFullScreen = repo.loadFullScreen() }
    fun setIsFullScreen(enabled: Boolean) {
        isFullScreen = enabled
        viewModelScope.launch { repo.setFullScreen(enabled) }
    }

    // dark mode
    var isDarkMode by mutableStateOf(false)
        private set
    val darkModeDescription
        get() = if (isDarkMode) uiDarkDescription else uiLightDescription
    fun initDarkMode(systemDefault: Boolean) =
        viewModelScope.launch { isDarkMode = repo.loadDarkMode(systemDefault) }
    fun setIsDarkMode(enabled: Boolean) {
        isDarkMode = enabled
        viewModelScope.launch { repo.setIsDarkMode(enabled) } }

    init {

        viewModelScope.launch(Dispatchers.Default) {

            // wait for system initialization of tts engine, takes a few seconds
            repo.isTtsInit.filter { it }.first()

            repo.initTtsSettings() // config text to speech with store preferences
            createBeautifulVoices() // add roman numerals to voice locale groups for ui
            setTtsDescriptions() // update descriptions for ui
        }

        // request mindfulness quote from network
        viewModelScope.launch(Dispatchers.IO) {

            _mindfulnessQuoteState.value = MindfulnessQuoteState.Loading
            try { _mindfulnessQuoteState.value = MindfulnessQuoteState.Success(repo.getMindfulnessQuote()) }
            catch (e: Exception) { _mindfulnessQuoteState.value = MindfulnessQuoteState.Error(e.message) }
        }
    }

    private fun setTtsDescriptions() {

        voiceDescription = beautifyVoiceName(repo.voice.name)
        speedDescription = repo.speed.toString()
        pitchDescription = repo.pitch.toString()
        queueDescription =
            if (isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }

    fun saveToWebhookClipboard(webhookUrl: String) = repo.saveToClipboard(webhookUrl)

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // todo refactor these beautiful voice name methods
    private fun createBeautifulVoices() {

        // group voices by locale
        voices = repo.voices.sortedBy { it.locale.toLanguageTag() }
        voiceIndex = voices.indexOfFirst { it.name == voice.name }

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
