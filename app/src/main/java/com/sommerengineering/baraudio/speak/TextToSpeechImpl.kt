package com.sommerengineering.baraudio.speak

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.core.os.bundleOf
import com.sommerengineering.baraudio.uitls.RomanNumerals
import com.sommerengineering.baraudio.uitls.volumeKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TextToSpeechImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    // system text to speech engine
    private val _textToSpeech = TextToSpeech(context, this)

    // flow initialization
    private var _isInit = MutableStateFlow(false)
    val isInit = _isInit.asStateFlow()

    // voice
    private lateinit var _voices: List<Voice>
    val voices
        get() = _voices
    private lateinit var _voice: Voice
    var voice
        get() = _voice
        set(value) {
            _voice = value
            _textToSpeech.voice = value
        }

    // speed
    private var _speed = 1f
    var speed
        get() = _speed
        set(value) {
            _speed = value
            _textToSpeech.setSpeechRate(value)
        }

    // pitch
    private var _pitch = 1f
    var pitch
        get() = _pitch
        set(value) {
            _pitch = value
            _textToSpeech.setPitch(value)
        }

    // queue behavior
    private var _isQueueAdd = true
    var isQueueAdd
        get() = _isQueueAdd
        set(value) {
            _isQueueAdd = value
        }

    // volume (mute)
    private var _volume = 1f
    var isMute
        get() = _volume == 0f
        set(value) {
            _volume = if (value) 0f else 1f
        }
    fun isSpeaking() = _textToSpeech.isSpeaking
    fun stop() = _textToSpeech.stop()

    suspend fun speak(
        timestamp: String,
        message: String) = suspendCancellableCoroutine { continuation ->

        // cancel on service destruction, etc
        continuation.invokeOnCancellation { _textToSpeech.stop() }

        // listen to speech progress
        val listener = object : UtteranceProgressListener() {
            override fun onDone(id: String?) {
                if (id != timestamp || !continuation.isActive) return
                continuation.resume(Unit)
            }
            override fun onError(id: String?) {
                if (id != timestamp || !continuation.isActive) return
                continuation.resume(Unit)
            }
            override fun onStart(id: String?) = Unit
        }

        _textToSpeech.setOnUtteranceProgressListener(listener)

        // speak message
        _textToSpeech.speak(
            normalizeMessage(message),
            _isQueueAdd.compareTo(false),
            bundleOf(volumeKey to _volume),
            timestamp)
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        _voices = _textToSpeech.voices.toList()
        _isInit.update { true }
    }

    // normalize message for speech: roman numerals and numbers to words
    private fun normalizeMessage(message: String) =
        replaceNumbers(
            replaceRomanNumerals(message))

    // guarantee engine speaks roman numerals correctly
    private val romanRegex =
        Regex("""\b(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|XIII|XIV|XV|XVI|XVII|XVIII|XIX|XX)\b""")
    private fun replaceRomanNumerals(text: String) =
        romanRegex.replace(text) { RomanNumerals.toWord(it.value) }

    // guarantee engine speaks numbers correctly, prevent "oh" instead of "zero", etc
    private val numberRegex =
        Regex("""-?\d+(\.\d+)?([eE][-+]?\d+)?""")
    private fun replaceNumbers(message: String) =
        numberRegex.replace(message) {
            val token = it.value // space delineated tokens
            if (token == "0" || "." in token) { numberToWord(token) }
            else token
        }
    private fun numberToWord(message: String): String {
        val builder = StringBuilder()
        for (char in message) {
            when (char) {
                '-' -> builder.append("minus ")
                '.' -> builder.append("point ")
                'e', 'E' -> builder.append("e ")
                '+' -> builder.append("plus ")
                '0' -> builder.append("zero ")
                '1' -> builder.append("one ")
                '2' -> builder.append("two ")
                '3' -> builder.append("three ")
                '4' -> builder.append("four ")
                '5' -> builder.append("five ")
                '6' -> builder.append("six ")
                '7' -> builder.append("seven ")
                '8' -> builder.append("eight ")
                '9' -> builder.append("nine ")
            }
        }
        return builder.toString().trim()
    }
}