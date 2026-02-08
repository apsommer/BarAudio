package com.sommerengineering.baraudio.hilt

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import com.sommerengineering.baraudio.cancelAllNotifications
import com.sommerengineering.baraudio.isQueueFlushKey
import com.sommerengineering.baraudio.pitchKey
import com.sommerengineering.baraudio.volumeKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    var _isInit = MutableStateFlow(false)
    val isInit = _isInit.asStateFlow()

    var voices: List<Voice> = emptyList()
        private set

    private lateinit var _voice: Voice
    private var _speed = 1f
    private var _pitch = 1f

    var voice
        get() = _voice
        set(value) {
            _voice = value
            textToSpeech.voice = value
        }

    var speed
        get() = _speed
        set(value) {
            _speed = value
            textToSpeech.setSpeechRate(value)
        }

    var pitch
        get() = _pitch
        set(value) {
            _pitch = value
            textToSpeech.setPitch(value)
        }

    var isQueueAdd by mutableStateOf(true)
    var volume by mutableFloatStateOf(0f)

    override fun onInit(status: Int) {

        // initialization complete
        if (status != TextToSpeech.SUCCESS) return

        isQueueAdd = readFromDataStore(context, isQueueFlushKey)?.toBooleanStrictOrNull() ?: true

        // attach progress listener to clear notifications
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) = cancelAllNotifications(context)

            // ignore
            override fun onDone(timestamp: String?) { }
            override fun onStop(timestamp: String?, isInterupted: Boolean) { }
            override fun onError(utteranceId: String?) { }
        })

        voices = textToSpeech.voices.toList()
        _isInit.update { true }
    }

    fun speak(
        timestamp: String,
        message: String,
        isForceVolume: Boolean = false) {

        if (message.isBlank() || !_isInit.value) return

        // config engine params
//        textToSpeech.setVoice(voice)
        textToSpeech.setSpeechRate(speed)
        textToSpeech.setPitch(pitch)
        val params =
            if (isForceVolume) bundleOf(volumeKey to 1f)
            else bundleOf(volumeKey to volume)

        // speak message
        textToSpeech.speak(
            message,
            isQueueAdd.compareTo(false),
            params,
            timestamp)
    }

    // mute
    fun isSpeaking() = textToSpeech.isSpeaking
    fun stop() = textToSpeech.stop()
}