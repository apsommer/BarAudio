package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import androidx.core.bundle.bundleOf
import kotlinx.coroutines.flow.MutableStateFlow

class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    val voice by lazy { mutableStateOf(textToSpeech.voice) }
    var speed by mutableStateOf(1f)
    var pitch by mutableStateOf(1f)
    var isQueueAdd by mutableStateOf(false)
    var volume by mutableStateOf(1f)

    var isInitialized = false
    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) { return }
        isInitialized = true

        voice.value =
            readFromDataStore(context, voiceKey)
                ?.let { name ->
                    textToSpeech
                        .voices
                        .first { it.name == name }
                } ?: textToSpeech.voice

        speed = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f
        isQueueAdd = readFromDataStore(context, isQueueFlushKey).toBoolean()

        // attach progress listener to clear notification when done speaking
        textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {

            override fun onDone(timestamp: String?) =
                clearNotification(timestamp)

            override fun onStop(timestamp: String?, isInterupted: Boolean) =
                clearNotification(timestamp)

            // do nothing
            override fun onStart(utteranceId: String?) { }
            override fun onError(utteranceId: String?) { }
        })
    }

    fun getVoices() =
        textToSpeech.voices

    fun clearNotification(timestamp: String?) {

        if (timestamp == null) return

        NotificationManagerCompat.from(context)
            .cancel(trimTimestamp(timestamp))
    }

    fun speak(
        timestamp: String,
        message: String) {

        if (message.isBlank() || !isInitialized) return

        // config engine params
        textToSpeech.setVoice(voice.value)
        textToSpeech.setSpeechRate(speed)
        textToSpeech.setPitch(pitch)
        val params = bundleOf(volumeKey to volume)

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