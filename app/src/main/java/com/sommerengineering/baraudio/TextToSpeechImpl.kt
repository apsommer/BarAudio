package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow

class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    var isQueueAdd = MutableStateFlow(false)
    var speed = MutableStateFlow(1f)
    var pitch = MutableStateFlow(1f)

    var isInitialized = false
    override fun onInit(status: Int) {

        if (status != TextToSpeech.SUCCESS) { return }

        isInitialized = true

        isQueueAdd.value = readFromDataStore(context, isQueueAddKey).toBoolean()
        speed.value = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch.value = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f
    }

    var message = ""
    fun speak() {

        if (message.isBlank() || !isInitialized) return

        // todo use high quality english voice for testing
        textToSpeech.voice = textToSpeech.voices
            .filter { it.quality >= 400 }
            .filter { it.locale.toString().contains("en") }
            .get(0)

        textToSpeech.setSpeechRate(speed.value)
        textToSpeech.setPitch(pitch.value)

        // speak message
        val status = textToSpeech.speak(
            message,
            isQueueAdd.value.compareTo(false),
            null,
            "42")

        if (status == TextToSpeech.ERROR) { logMessage("Text-to-speech error [$status]") }
        else logMessage("Text-to-speech message spoken: $message")

        // clear unspoken message container
        message = ""
    }

    fun getVoices(): Set<Voice> {
        return textToSpeech.voices
    }
}