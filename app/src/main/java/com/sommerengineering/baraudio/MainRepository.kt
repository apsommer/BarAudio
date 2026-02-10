package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sommerengineering.baraudio.hilt.FirebaseDatabaseImpl
import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.dataStore
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.messages.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl,
    val firebaseDatabase: FirebaseDatabaseImpl,
) {

    // database
    val messages = firebaseDatabase.messages
    fun startListening() = firebaseDatabase.startListening()
    fun deleteMessage(message: Message) = firebaseDatabase.deleteMessage(message)
    fun deleteAllMessages() = firebaseDatabase.deleteAllMessages()
    fun stopListening() = firebaseDatabase.stopListening()

    // text-to-speech
    val isTtsInit = tts.isInit

    fun initTtsSettings() {

        // config tts with saved preferences
        tts.voice = readFromDataStore(context, voiceKey)
            ?.let { preference -> voices.firstOrNull { it.name == preference }}
            ?: voices.firstOrNull { it.name == defaultVoice }
                    ?: voice

        tts.speed = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        tts.pitch = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f
        tts.isQueueAdd = readFromDataStore(context, isQueueFlushKey)?.toBooleanStrictOrNull() ?: true

        val isMute = _isMute.value
        tts.volume = if (isMute) 0f else 1f
    }

    fun speakMessage(
        message: String) = tts.speak(
            timestamp = "",
            message = message,
            isForceVolume = true)

    // voice
    val voices
        get() = tts.voices
    var voice
        get() = tts.voice
        set(value) {
            tts.voice = value
            writeToDataStore(context, voiceKey, value.name)
        }

    // speed
    var speed
        get() = tts.speed
        set(value) {
            val roundedSpeed = ((value* 10).roundToInt()).toFloat() / 10
            tts.speed = roundedSpeed
            writeToDataStore(context, speedKey, roundedSpeed.toString())
        }

    // pitch
    var pitch
        get() = tts.pitch
        set(value) {
            val roundedPitch = ((value * 10).roundToInt()).toFloat() / 10
            tts.pitch = roundedPitch
            writeToDataStore(context, pitchKey, roundedPitch.toString())
        }

    // queue behavior
    var isQueueAdd
        get() = tts.isQueueAdd
        set(value) {
            tts.isQueueAdd = value
            writeToDataStore(context, isQueueFlushKey, value.toString())
        }

    // mute button
    private val _isMute = MutableStateFlow(false) // default unmuted
    val isMute = _isMute.asStateFlow()
    fun toggleMute() = setMute(!_isMute.value)
    private fun setMute(
        isMute: Boolean) {

        _isMute.update { isMute }

        // set volume
        if (isMute) { tts.volume = 0f }
        else { tts.volume = 1f }

        // stop any current speech
        if (isMute && tts.isSpeaking()) { tts.stop() }

        writeToDataStore(context, volumeKey, tts.volume.toString())
    }

    // mindfulness quote
    private val _isShowQuote = MutableStateFlow(false)
    val isShowQuote = _isShowQuote.asStateFlow()
    fun showQuote(isChecked: Boolean) {
        _isShowQuote.update { isChecked }
        writeToDataStore(context, showQuoteKey, isChecked.toString())
    }
    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()

    // futures webhooks
    private val _isFuturesWebhooks = MutableStateFlow(false)
    val isFuturesWebhooks = _isFuturesWebhooks.asStateFlow()
    fun setFuturesWebhooks(isChecked: Boolean) {
        _isFuturesWebhooks.update { isChecked }
        writeToDataStore(context, isFuturesWebhooksKey, isChecked.toString())
        writeWhitelistToDatabase(isChecked)
    }

    // full screen
    suspend fun loadFullScreen(): Boolean {
        val key = booleanPreferencesKey(isFullScreenKey)
        return context.dataStore.data.first()[key] ?: false
    }
    suspend fun setFullScreen(enabled: Boolean) =
        context.dataStore.edit {
            it[booleanPreferencesKey(isFullScreenKey)] = enabled
        }

    // dark mode
    suspend fun loadDarkMode(systemDefault: Boolean): Boolean {
        val key = booleanPreferencesKey(isDarkModeKey)
        return context.dataStore.data.first()[key] ?: systemDefault
    }
    suspend fun setIsDarkMode(enabled: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(isDarkModeKey)] = enabled
        }
    }

    init {

        // todo load from prefs
        _isMute.update { readFromDataStore(context, volumeKey)?.toFloat() == 0f }
        _isShowQuote.update { readFromDataStore(context, showQuoteKey)?.toBooleanStrictOrNull() ?: true }
        _isFuturesWebhooks.update { readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true }
    }

    fun saveToClipboard(
        webhookUrl: String) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)

        // toast for older api
        if (31 > android.os.Build.VERSION.SDK_INT) {
            Toast.makeText(
                context,
                webhookUrl,
                Toast.LENGTH_SHORT)
                .show()
        }
    }

}