package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech

class TextToSpeechImpl(
    activityContext: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(activityContext, this)
    var isInitialized = false
    var message = ""

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            isInitialized = true

            // todo use high quality english voice for testing
            textToSpeech.voice = textToSpeech.voices
                .filter { it.quality >= 400 }
                .filter { it.locale.toString().contains("en")}
                .get(0)

            logMessage("Text-to-speech initialized")
            announceMessage()
        }
    }

    fun announceMessage() {

        if (message.isBlank() || !isInitialized) return

        val status = textToSpeech.speak(message, 1, null, "42")
        if (status == TextToSpeech.ERROR) { logMessage("Text-to-speech error [$status]") }

        message = ""
    }

}