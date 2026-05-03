package com.sommerengineering.signalvoice.speak

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.core.os.bundleOf
import com.sommerengineering.signalvoice.uitls.RomanNumerals
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

const val volumeKey = TextToSpeech.Engine.KEY_PARAM_VOLUME

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

    suspend fun speakQueued(
        timestamp: String,
        message: String
    ) = suspendCancellableCoroutine { continuation ->

        // listen to speech progress
        val listener = object : UtteranceProgressListener() {

            override fun onStart(id: String?) = Unit

            // cancel coroutine on completion, error, or stop
            override fun onDone(id: String?) = finishCoroutine(id)
            override fun onStop(id: String?, interrupted: Boolean) = finishCoroutine(id)
            override fun onError(id: String?) = finishCoroutine(id)
            private fun finishCoroutine(id: String?) {
                if (id != timestamp || !continuation.isActive) return
                continuation.resume(Unit)
            }
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

    fun speakImmediate(utterance: String) =
        _textToSpeech.speak(
            normalizeMessage(utterance),
            TextToSpeech.QUEUE_FLUSH,
            bundleOf(volumeKey to 1f),
            System.currentTimeMillis().toString()
        )

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
        spokenText = Regex("""[+-]?\d+(\.\d+)?%?""")
            .replace(spokenText) { match ->

                val raw = match.value

                val isPercent = raw.endsWith("%")
                val clean = raw.removeSuffix("%")

                val sign = when {
                    clean.startsWith("+") -> "plus "
                    clean.startsWith("-") -> "minus "
                    else -> ""
                }

                val number = clean.trimStart('+', '-')

                val spokenNumber =
                    if (number.contains(".")) {
                        val (intPart, decPart) = number.split(".")
                        val decimals = decPart.map { digit ->
                            units[digit.digitToInt()]
                        }.joinToString(" ")
                        "${intPart.toInt()} point $decimals"
                    } else {
                        number.toInt().toString()
                    }

                buildString {
                    append(sign)
                    append(spokenNumber)
                    if (isPercent) append(" percent")
                }
            }

        return spokenText
    }

    private val units = listOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
        "sixteen", "seventeen", "eighteen", "nineteen"
    )
}