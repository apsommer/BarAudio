package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import org.koin.java.KoinJavaComponent.inject

class TextToSpeechImpl(
    private val context: Context
) : TextToSpeech.OnInitListener {

    val viewModel: MainViewModel by inject(MainViewModel::class.java)

    private val textToSpeech = TextToSpeech(context, this)
    var isInitialized = false
    var message = ""

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            isInitialized = true
            logMessage("Text-to-speech initialized")

            // configure engine settings
            viewModel.initConfig(context)
            configure()

            // speak any messages waiting in queue
            speakMessage()
        }
    }

    fun configure() {

        // todo use high quality english voice for testing
        textToSpeech.voice = textToSpeech.voices
            .filter { it.quality >= 400 }
            .filter { it.locale.toString().contains("en") }
            .get(0)

        // set speed
        textToSpeech.setSpeechRate(viewModel.getSpeed())
        textToSpeech.setPitch(viewModel.getPitch())

        logMessage("Text-to-speech configured")
    }

    fun speakMessage() {

        if (message.isBlank() || !isInitialized) return

        // speak message
        val status = textToSpeech.speak(message, 1, null, "42")
        if (status == TextToSpeech.ERROR) { logMessage("Text-to-speech error [$status]") }
        logMessage("Text-to-speech message spoken: $message")

        // clear unspoken message container
        message = ""
    }

    fun getVoices(): Set<Voice> {
        return textToSpeech.voices
    }
}