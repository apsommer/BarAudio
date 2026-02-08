package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val rapidApi: RapidApi,
    val tts: TextToSpeechImpl,
) {

    val isTtsInit = tts.isInit

    val voices
        get() = tts.voices

    var voice
        get() = tts.voice
        set(value) {

            tts.voice = value
            writeToDataStore(context, voiceKey, value.name)
            speakLastMessage()
        }

    var speed
        get() = tts.speed
        set(value) {

            val roundedSpeed = ((value* 10).roundToInt()).toFloat() / 10
            tts.speed = roundedSpeed
            writeToDataStore(context, speedKey, roundedSpeed.toString())
        }

    var pitch
        get() = tts.pitch
        set(value) {
            val roundedPitch = ((value * 10).roundToInt()).toFloat() / 10
            tts.pitch = roundedPitch
            writeToDataStore(context, pitchKey, roundedPitch.toString())
        }

    fun initTtsSettings() {

        // get voice from preferences, or default
        tts.voice = readFromDataStore(context, voiceKey)
            ?.let { preference -> tts.voices.firstOrNull { it.name == preference }}
            ?: tts.voices.firstOrNull { it.name == defaultVoice }
            ?: tts.voice

        tts.speed = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        tts.pitch = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f

        _pitchDescription.update { tts.pitch.toString() }
        _queueDescription.update {
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
        }

        val isMute = _isMute.value
        tts.volume = if (isMute) 0f else 1f
    }

    private val _messages = SnapshotStateList<Message>()
    val messages = _messages

    private val _pitchDescription = MutableStateFlow("")
    val pitchDescription = _pitchDescription.asStateFlow()

    private val _queueDescription = MutableStateFlow("")
    val queueDescription = _queueDescription.asStateFlow()

    private val _isMute = MutableStateFlow(false) // default unmuted
    val isMute = _isMute.asStateFlow()

    private val _isShowQuote = MutableStateFlow(false)
    val isShowQuote = _isShowQuote.asStateFlow()

    val isFullScreen: Flow<Boolean> =
        context.dataStore.data.map {
            it[booleanPreferencesKey(isFullScreenKey)] ?: false
        }

    suspend fun setFullScreen(enabled: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(isFullScreenKey)] = enabled
        }
    }

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _isFuturesWebhooks = MutableStateFlow(false)
    val isFuturesWebhooks = _isFuturesWebhooks.asStateFlow()

    // load local cache from data store
    init {

        _isMute.update { readFromDataStore(context, volumeKey)?.toFloat() == 0f }
        _isShowQuote.update { readFromDataStore(context, showQuoteKey)?.toBooleanStrictOrNull() ?: true }
        _isFuturesWebhooks.update { readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true }
    }

    //
    fun setFuturesWebhooks(
        isChecked: Boolean
    ) {

        _isFuturesWebhooks.update { isChecked }
        writeToDataStore(context, isFuturesWebhooksKey, isChecked.toString())
        writeWhitelistToDatabase(isChecked)
    }

    // quote
    fun showQuote(
        isChecked: Boolean) {

        _isShowQuote.update { isChecked }
        writeToDataStore(context, showQuoteKey, isChecked.toString())
    }

    fun speakLastMessage() {

        val messages = _messages

        val lastMessage =
            if (messages.isEmpty()) defaultMessage
            else messages.last().message

        tts.speak(
            timestamp = "",
            message = lastMessage,
            isForceVolume = true)
    }

    fun toggleMute() {

        val currentMute = _isMute.value
        setMute(!currentMute) // swap
    }

    private fun setMute(
        newMute: Boolean) {

        _isMute.update { newMute }

        // set volume
        if (newMute) { tts.volume = 0f }
        else { tts.volume = 1f }

        // stop current speaking
        if (newMute && tts.isSpeaking()) { tts.stop() }

        writeToDataStore(context, volumeKey, tts.volume.toString())
    }

    fun isQueueAdd() = tts.isQueueAdd
    fun setIsQueueAdd(
        isChecked: Boolean) {

        tts.isQueueAdd = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())

        _queueDescription.update {
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
        }
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

    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
}