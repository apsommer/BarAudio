package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow

class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    val voice by lazy { mutableStateOf(textToSpeech.voice) }

    // todo refactor to mutableStateOf ... no need for Flow and .collectAsState()
    var speed = MutableStateFlow(1f)
    var pitch = MutableStateFlow(1f)
    var isQueueAdd = MutableStateFlow(false)

    var isInitialized = false
    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) { return }

        isInitialized = true

        getVoiceFromDataStore()
        speed.value = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch.value = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f
        isQueueAdd.value = readFromDataStore(context, isQueueAddKey).toBoolean()

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

    private fun getVoiceFromDataStore() {

        val voiceName = readFromDataStore(context, voiceKey)

        try {
            voiceName?.let { voice.value =
                textToSpeech.voices
                    .first { it.name == voiceName }
            }
        } catch (e: NoSuchElementException) { logException(e) }
    }

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
        textToSpeech.setSpeechRate(speed.value)
        textToSpeech.setPitch(pitch.value)

        // speak message
        textToSpeech.speak(
            message,
            isQueueAdd.value.compareTo(false),
            null,
            timestamp)
    }

    fun getVoices(): Set<Voice> {
        return textToSpeech.voices
    }
}