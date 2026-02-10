package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sommerengineering.baraudio.hilt.ApplicationScope
import com.sommerengineering.baraudio.hilt.FirebaseDatabaseImpl
import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.dataStore
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.messages.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl,
    val firebaseDatabase: FirebaseDatabaseImpl,
) {

    // database
    val messages = firebaseDatabase.messages
    fun startListeningToDatabase() = firebaseDatabase.startListening()
    fun deleteMessage(message: Message) = firebaseDatabase.deleteMessage(message)
    fun deleteAllMessages() = firebaseDatabase.deleteAllMessages()
    fun stopListening() = firebaseDatabase.stopListening()

    // text-to-speech
    val isTtsInit = tts.isInit
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

    init {
        appScope.launch {
            isTtsInit.filter { it }.first()
            initTtsSettings()
            _isTtsReady.update { true }
        }
    }

    // voice
    val voices
        get() = tts.voices
    var voice
        get() = tts.voice
        set(value) {
            tts.voice = value
            writePreference(stringPreferencesKey(voiceNameKey), value.name)
        }

    // speed
    var speed
        get() = tts.speed
        set(value) {
            val roundedSpeed = ((value* 10).roundToInt()).toFloat() / 10
            tts.speed = roundedSpeed
            writePreference(floatPreferencesKey(speedKey), roundedSpeed)
        }

    // pitch
    var pitch
        get() = tts.pitch
        set(value) {
            val roundedPitch = ((value * 10).roundToInt()).toFloat() / 10
            tts.pitch = roundedPitch
            writePreference(floatPreferencesKey(pitchKey), roundedPitch)
        }

    // queue behavior
    var isQueueAdd
        get() = tts.isQueueAdd
        set(value) {
            tts.isQueueAdd = value
            writePreference(booleanPreferencesKey(isQueueAddKey), value)
        }

    // mute
    var isMute
        get() = tts.isMute
        set(value) {
            tts.isMute = value
            if (value && tts.isSpeaking()) { tts.stop() } // stop any current speech
            writePreference(booleanPreferencesKey(isMuteKey), value)
        }

    fun speakMessage(
        message: String) = tts.speak(
        timestamp = "",
        message = message,
        isForceVolume = true)

    suspend fun initTtsSettings() {

        tts.voice = readPreference(stringPreferencesKey(voiceNameKey))
            ?.let { preference -> voices.firstOrNull { it.name == preference }}
            ?: voices.firstOrNull { it.name == defaultVoice }
                    ?: voice
        tts.speed = readPreference(floatPreferencesKey(speedKey)) ?: 1f
        tts.pitch = readPreference(floatPreferencesKey(pitchKey)) ?: 1f
        tts.isQueueAdd = readPreference(booleanPreferencesKey(isQueueAddKey)) ?: true
        tts.isMute = readPreference(booleanPreferencesKey(isMuteKey)) ?: false

    }

    // mindfulness quote
    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
    suspend fun loadShowQuote() =
        readPreference(booleanPreferencesKey(isShowQuoteKey)) ?: true
    fun updateShowQuote(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isShowQuoteKey), enabled)

    // futures webhooks
    suspend fun loadFuturesWebhooks() =
        readPreference(booleanPreferencesKey(isFuturesWebhooksKey)) ?: true
    fun updateFuturesWebhooks(enabled: Boolean) {
        writePreference(booleanPreferencesKey(isFuturesWebhooksKey), enabled)
        writeWhitelistToDatabase(enabled)
    }

    // full screen
    suspend fun loadFullScreen() =
        readPreference(booleanPreferencesKey(isFullScreenKey)) ?: false
    fun updateFullScreen(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isFullScreenKey), enabled)

    // dark mode
    suspend fun loadDarkMode(systemDefault: Boolean): Boolean {
        val key = booleanPreferencesKey(isDarkModeKey)
        return context.dataStore.data.first()[key] ?: systemDefault
    }
    fun updateDarkMode(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isDarkModeKey), enabled)

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

    suspend fun <T> readPreference(
        key: Preferences.Key<T>) : T? =
        context.dataStore.data.first()[key]

    fun <T> writePreference(
        key: Preferences.Key<T>,
        value: T) =
        appScope.launch {
            context.dataStore.edit { it[key] = value }
        }
}