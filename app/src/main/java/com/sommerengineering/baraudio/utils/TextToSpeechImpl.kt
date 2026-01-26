package com.sommerengineering.baraudio.utils

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
import com.sommerengineering.baraudio.speedKey
import com.sommerengineering.baraudio.voiceKey
import com.sommerengineering.baraudio.volumeKey
import kotlinx.coroutines.flow.MutableStateFlow

class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    val voice by lazy { mutableStateOf(textToSpeech.voice) }
    var speed by mutableFloatStateOf(1f)
    var pitch by mutableFloatStateOf(1f)
    var isQueueAdd by mutableStateOf(true)
    var volume by mutableFloatStateOf(0f)

    var isInit = MutableStateFlow(false)

    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) { return }

        // init voice
        voice.value = readFromDataStore(context, voiceKey)
            ?.let { preference -> textToSpeech.voices.firstOrNull { it.name == preference }}
            ?: textToSpeech.voices.firstOrNull { it.name == "en-gb-x-gbd-local" } // british, male
            ?: textToSpeech.voice

        speed = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f
        isQueueAdd = readFromDataStore(context, isQueueFlushKey)?.toBooleanStrictOrNull() ?: true

        // attach progress listener to clear notifications
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onStart(utteranceId: String?) = cancelAllNotifications(context)

            // ignore
            override fun onDone(timestamp: String?) { }
            override fun onStop(timestamp: String?, isInterupted: Boolean) { }
            override fun onError(utteranceId: String?) { }
        })

        isInit.value = true
    }

    fun getVoices(): Set<Voice> =
        textToSpeech.voices

    fun speak(
        timestamp: String,
        message: String,
        isForceVolume: Boolean = false) {

        if (message.isBlank() || !isInit.value) return

        // config engine params
        textToSpeech.setVoice(voice.value)
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