package com.sommerengineering.baraudio.hilt

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import com.sommerengineering.baraudio.cancelAllNotifications
import com.sommerengineering.baraudio.volumeKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update



class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val _textToSpeech = TextToSpeech(context, this)
    private var _isInit = MutableStateFlow(false)
    private lateinit var _voices: List<Voice>
    private lateinit var _voice: Voice
    private var _speed = 1f
    private var _pitch = 1f
    private var _isQueueAdd = true

    val isInit = _isInit.asStateFlow()

    val voices
        get() = _voices

    var voice
        get() = _voice
        set(value) {
            _voice = value
            _textToSpeech.voice = value
        }

    var speed
        get() = _speed
        set(value) {
            _speed = value
            _textToSpeech.setSpeechRate(value)
        }

    var pitch
        get() = _pitch
        set(value) {
            _pitch = value
            _textToSpeech.setPitch(value)
        }

    var isQueueAdd
        get() = _isQueueAdd
        set(value) {
            _isQueueAdd = value
        }

    var volume by mutableFloatStateOf(0f)

    override fun onInit(status: Int) {

        // initialization complete
        if (status != TextToSpeech.SUCCESS) return

        _voices = _textToSpeech.voices.toList()
        _isInit.update { true }

        // attach progress listener to clear notifications
        _textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) = cancelAllNotifications(context)

            // ignore
            override fun onDone(timestamp: String?) { }
            override fun onStop(timestamp: String?, isInterupted: Boolean) { }
            override fun onError(utteranceId: String?) { }
        })
    }

    fun speak(
        timestamp: String,
        message: String,
        isForceVolume: Boolean = false) {

        if (message.isBlank() || !_isInit.value) return

        // config engine params
        val params =
            if (isForceVolume) bundleOf(volumeKey to 1f)
            else bundleOf(volumeKey to volume)

        // speak message
        _textToSpeech.speak(
            message,
            _isQueueAdd.compareTo(false),
            params,
            timestamp)
    }

    // mute
    fun isSpeaking() = _textToSpeech.isSpeaking
    fun stop() = _textToSpeech.stop()
}