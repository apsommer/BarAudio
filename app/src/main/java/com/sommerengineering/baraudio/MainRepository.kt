package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sommerengineering.baraudio.hilt.ApplicationScope
import com.sommerengineering.baraudio.hilt.FirebaseDatabaseImpl
import com.sommerengineering.baraudio.hilt.RapidApi
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.dataStore
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.messages.Message
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            appScope.launch { writePreference(
                stringPreferencesKey(voiceKey),
                value.name)
            }
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
    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
    suspend fun loadIsShowQuote() =
        readPreference(booleanPreferencesKey(isShowQuoteKey)) ?: true
    fun setIsShowQuote(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isShowQuoteKey), enabled)

    // futures webhooks
    suspend fun loadIsFuturesWebhooks() =
        readPreference(booleanPreferencesKey(isFuturesWebhooksKey)) ?: true
    fun setIsFuturesWebhooks(enabled: Boolean) {
        writePreference(booleanPreferencesKey(isFuturesWebhooksKey), enabled)
        writeWhitelistToDatabase(enabled)
    }

    // full screen
    suspend fun loadFullScreen() =
        readPreference(booleanPreferencesKey(isFullScreenKey)) ?: false
    fun setFullScreen(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isFullScreenKey), enabled)

    // dark mode
    suspend fun loadDarkMode(systemDefault: Boolean): Boolean {
        val key = booleanPreferencesKey(isDarkModeKey)
        return context.dataStore.data.first()[key] ?: systemDefault
    }
    fun setIsDarkMode(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isDarkModeKey), enabled)

    init {

        // todo load from prefs
        _isMute.update { readFromDataStore(context, volumeKey)?.toFloat() == 0f }
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