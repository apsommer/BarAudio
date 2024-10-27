package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech

class TextToSpeechImpl(
    activityContext: Context
) : TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(activityContext, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            logMessage("Initialize text-to-speech engine")
        }
    }

    fun announceMessage(message: String, ) {

        val status = textToSpeech.speak(message, 1, null, "42")
        if (status == TextToSpeech.ERROR) { logMessage("Error from text-to-speech") }
    }
}