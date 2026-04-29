package com.sommerengineering.signalvoice.speak

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.core.os.bundleOf
import com.ibm.icu.text.RuleBasedNumberFormat
import com.sommerengineering.signalvoice.uitls.RomanNumerals
import com.sommerengineering.signalvoice.uitls.volumeKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
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
        message: String
    ) = suspendCancellableCoroutine { continuation ->

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
            TextToSpeech.QUEUE_ADD,
            bundleOf(volumeKey to _volume),
            timestamp
        )
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        _voices = _textToSpeech.voices.toList()
        _isInit.update { true }
    }

    fun normalizeMessage(message: String): String {

        // punctuation
        var spokenText = message
            .replace(Regex("""\s*•\s*"""), ", ") // bullet to comma
            .replace(Regex("""(?<=\d),(?=\d)"""), "") // remove thousands separators

        // roman numerals to words, handle voice names
        spokenText =
            Regex("""\b(I|II|III|IV|V|VI|VII|VIII|IX|X|XI|XII|XIII|XIV|XV|XVI|XVII|XVIII|XIX|XX)\b""")
                .replace(spokenText) { RomanNumerals.toWord(it.value) }

        // 'm' to minutes, 123m -> 123 minutes
        spokenText = Regex("""\b(\d+)m\b""")
            .replace(spokenText) {
                val value = it.groupValues[1]
                if (value == "1") "$value minute" else "$value minutes"
            }

        // numbers to words, prevent "oh" instead of "zero"
        spokenText = Regex("""-?\d[\d,]*(\.\d+)?""")
            .replace(spokenText) {
                val number = it.value.toDouble()
                RuleBasedNumberFormat(Locale.US, RuleBasedNumberFormat.SPELLOUT)
                    .format(number)
            }

        return spokenText
    }
}