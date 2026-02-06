package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.dataStore
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.messages.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl,
) {

    // todo move?
    private val beautifulVoiceNames = hashMapOf<String, String>()

    private val _voices = MutableStateFlow<List<Voice>>(emptyList())
    val voices = _voices.asStateFlow()

    private val _messages = SnapshotStateList<Message>()
    val messages = _messages

    private val _voiceDescription = MutableStateFlow("")
    val voiceDescription = _voiceDescription.asStateFlow()

    private val _speedDescription = MutableStateFlow("")
    val speedDescription = _speedDescription.asStateFlow()

    private val _pitchDescription = MutableStateFlow("")
    val pitchDescription = _pitchDescription.asStateFlow()

    private val _queueDescription = MutableStateFlow("")
    val queueDescription = _queueDescription.asStateFlow()

    private val _isMute = MutableStateFlow(false) // default unmuted
    val isMute = _isMute.asStateFlow()

    private val _isShowQuote = MutableStateFlow(false)
    val isShowQuote = _isShowQuote.asStateFlow()

    val isFullScreen: Flow<Boolean> =
        context.dataStore.data.map {
            it[booleanPreferencesKey(isFullScreenKey)] ?: false
        }

    suspend fun setFullScreen(enabled: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(isFullScreenKey)] = enabled
        }
    }

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _isFuturesWebhooks = MutableStateFlow(false)
    val isFuturesWebhooks = _isFuturesWebhooks.asStateFlow()

    // load local cache from data store
    init {
        _isMute.update { readFromDataStore(context, volumeKey)?.toFloat() == 0f }
        _isShowQuote.update { readFromDataStore(context, showQuoteKey)?.toBooleanStrictOrNull() ?: true }
        _isFuturesWebhooks.update { readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true }
    }

    //
    fun setFuturesWebhooks(
        isChecked: Boolean
    ) {

        _isFuturesWebhooks.update { isChecked }
        writeToDataStore(context, isFuturesWebhooksKey, isChecked.toString())
        writeWhitelistToDatabase(isChecked)
    }

    // quote
    fun showQuote(
        isChecked: Boolean) {

        _isShowQuote.update { isChecked }
        writeToDataStore(context, showQuoteKey, isChecked.toString())
    }

    fun observeTtsInit(onInit: () -> Unit) =
        tts.isInit
            .filter { it }
            .distinctUntilChanged()
            .onEach { onInit() }

    fun initTtsSettings() {

        // get voices from engine
        val voices = tts.getVoices().sortedBy { it.locale.displayName }
        _voices.value = voices

        // add roman numerals to voice locale groups
        createBeautifulVoices()

        _voiceDescription.update { beautifyVoiceName(tts.voice.value.name) }
        _speedDescription.update { tts.speed.toString() }
        _pitchDescription.update { tts.pitch.toString() }
        _queueDescription.update {
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
        }

        val isMute = _isMute.value
        tts.volume = if (isMute) 0f else 1f
    }

    private fun createBeautifulVoices() {

        // group voices by locale
        val voices = voices.value
        val groupedByLocaleVoices = voices.groupBy { it.locale.displayName }

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

    fun getVoiceIndex() : Int {
        val voices = _voices.value
        return voices.indexOf(
            voices.find {
                it == tts.voice.value
            })
    }

    fun setVoice(
        voice: Voice) {

        tts.voice.value = voice
        _voiceDescription.update { beautifyVoiceName(voice.name) }
        writeToDataStore(context, voiceKey, voice.name)
        speakLastMessage()
    }

    fun speakLastMessage() {

        val messages = _messages

        val lastMessage =
            if (messages.isEmpty()) defaultMessage
            else messages.last().message

        tts.speak(
            timestamp = "",
            message = lastMessage,
            isForceVolume = true)
    }

    fun toggleMute() {

        val currentMute = _isMute.value
        setMute(!currentMute) // swap
    }

    private fun setMute(
        newMute: Boolean) {

        _isMute.update { newMute }

        // set volume
        if (newMute) { tts.volume = 0f }
        else { tts.volume = 1f }

        // stop current speaking
        if (newMute && tts.isSpeaking()) { tts.stop() }

        writeToDataStore(context, volumeKey, tts.volume.toString())
    }

    fun getSpeed() = tts.speed
    fun setSpeed(
        rawSpeed: Float) {

        val roundedSpeed = ((rawSpeed * 10).roundToInt()).toFloat() / 10
        tts.speed = roundedSpeed
        writeToDataStore(context, speedKey, roundedSpeed.toString())
        _speedDescription.update { roundedSpeed.toString() }
    }

    fun getPitch() = tts.pitch
    fun setPitch(
        rawPitch: Float) {

        val roundedPitch = ((rawPitch * 10).roundToInt()).toFloat() / 10
        tts.pitch = roundedPitch
        writeToDataStore(context, pitchKey, roundedPitch.toString())
        _pitchDescription.update { roundedPitch.toString() }
    }

    fun isQueueAdd() = tts.isQueueAdd
    fun setIsQueueAdd(
        isChecked: Boolean) {

        tts.isQueueAdd = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())

        _queueDescription.update {
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
        }
    }

    fun saveToClipboard(
        webhookUrl: String) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)

        // toast for older api
        if (31 > android.os.Build.VERSION.SDK_INT) {
            Toast.makeText(
                context,
                webhookUrl,
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
}