package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech

class TextToSpeechImpl(
    activityContext: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(activityContext, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            // todo use high quality english voice for testing
            textToSpeech.voice = textToSpeech.voices
                .filter { it.quality >= 400 }
                .filter { it.locale.toString().contains("en")}
                .get(0)

            logMessage("Text-to-speech engine initialized")
        }
    }

    fun announceMessage(message: String, ) {

        val status = textToSpeech.speak(message, 1, null, "42")
        if (status == TextToSpeech.ERROR) { logMessage("Text-to-speech engine ERROR") }
    }
}