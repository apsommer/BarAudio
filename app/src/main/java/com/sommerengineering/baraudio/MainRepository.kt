package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.messages.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl,
) {

    private val _voices = MutableStateFlow<List<Voice>>(emptyList())
    val voices = _voices.asStateFlow()

    private val _messages = SnapshotStateList<Message>()
    val messages = _messages

    private var _voiceDescription by mutableStateOf("")
    val voiceDescription get() = _voiceDescription

    private var _speedDescription by mutableStateOf("")
    val speedDescription get() = _speedDescription

    private var _pitchDescription by mutableStateOf("")
    val pitchDescription get() = _pitchDescription

    private var _queueDescription by mutableStateOf("")
    val queueDescription get() = _queueDescription

    private var _isMute by mutableStateOf(false) // default unmuted
    val isMute get() = _isMute

    private val beautifulVoiceNames = hashMapOf<String, String>()

    fun observeTtsInit(onInit: () -> Unit) =
        tts.isInit
            .filter { it }
            .distinctUntilChanged()
            .onEach { onInit() }

    fun initTtsSettings() {

        // get voices from engine
        val voices = tts.getVoices().sortedBy { it.locale.displayName }
        _voices.value = voices

        // group voices by locale
        val groupedByLocaleVoices = voices.groupBy { it.locale.displayName }

        // add roman numeral to name
        groupedByLocaleVoices.keys
            .forEach { localeGroup ->
                val localeVoices = groupedByLocaleVoices[localeGroup] ?: return@forEach
                localeVoices.forEachIndexed { i, voice ->
                    beautifulVoiceNames[voice.name] = enumerateVoices(voice, i)
                }
            }

        _voiceDescription = beautifyVoiceName(tts.voice.value.name)
        _speedDescription = tts.speed.toString()
        _pitchDescription = tts.pitch.toString()
        _queueDescription =
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription

        // todo mute button ui must be faster
        tts.volume = readFromDataStore(context, volumeKey)?.toFloat() ?: 0f
        _isMute = tts.volume == 0f
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
        writeToDataStore(context, voiceKey, voice.name)
        _voiceDescription = beautifyVoiceName(voice.name)
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

    fun toggleMute() = setMute(!_isMute)

    private fun setMute(
        newMute: Boolean) {

        _isMute = newMute

        if (_isMute) { tts.volume = 0f }
        else { tts.volume = 1f }

        if (_isMute && tts.isSpeaking()) { tts.stop() }

        writeToDataStore(context, volumeKey, tts.volume.toString())
    }

    fun getSpeed() = tts.speed
    fun setSpeed(
        rawSpeed: Float) {

        val selectedSpeed = ((rawSpeed * 10).roundToInt()).toFloat() / 10
        tts.speed = selectedSpeed
        writeToDataStore(context, speedKey, selectedSpeed.toString())
        _speedDescription = selectedSpeed.toString()
    }

    fun getPitch() = tts.pitch
    fun setPitch(
        rawPitch: Float) {

        val selectedPitch = ((rawPitch * 10).roundToInt()).toFloat() / 10
        tts.pitch = selectedPitch
        writeToDataStore(context, pitchKey, selectedPitch.toString())
        _pitchDescription = selectedPitch.toString()
    }

    fun isQueueAdd() = tts.isQueueAdd
    fun setIsQueueAdd(
        isChecked: Boolean) {

        tts.isQueueAdd = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())

        _queueDescription =
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }










    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
}