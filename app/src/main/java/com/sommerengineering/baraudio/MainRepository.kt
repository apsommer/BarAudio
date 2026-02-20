package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging
import com.sommerengineering.baraudio.firebase.FirebaseDatabaseImpl
import com.sommerengineering.baraudio.messages.RapidApi
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.room.RoomImpl
import com.sommerengineering.baraudio.uitls.defaultVoice
import com.sommerengineering.baraudio.uitls.isDarkModeKey
import com.sommerengineering.baraudio.uitls.isFullScreenKey
import com.sommerengineering.baraudio.uitls.isMuteKey
import com.sommerengineering.baraudio.uitls.isNQKey
import com.sommerengineering.baraudio.uitls.isQueueAddKey
import com.sommerengineering.baraudio.uitls.isShowQuoteKey
import com.sommerengineering.baraudio.uitls.nqTopic
import com.sommerengineering.baraudio.uitls.onboardingKey
import com.sommerengineering.baraudio.uitls.pitchKey
import com.sommerengineering.baraudio.uitls.recentMessageTimeMillis
import com.sommerengineering.baraudio.uitls.speedKey
import com.sommerengineering.baraudio.uitls.voiceNameKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    val tts: TextToSpeechImpl,
    val roomDb: RoomImpl,
    val firebaseDb: FirebaseDatabaseImpl,
    val dataStore: DataStore<Preferences>,
    val rapidApi: RapidApi) {

    // room database
    val messages = roomDb.messages
        .stateIn(appScope, SharingStarted.Eagerly, emptyList())
    fun deleteMessage(message: Message) = roomDb.deleteMessage(message)
    fun deleteAllMessages() = roomDb.deleteAllMessages()
    fun addMessage(message: Message) = roomDb.addMessage(message)

    // text-to-speech
    private val isTtsInit = tts.isInit
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady = _isTtsReady.asStateFlow()

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
    fun speakMessage(message: Message) {

        // system calls this from FCM onMessageReceived() when,
            // app foreground or background
            // phone connects to network after being offline

        // ignore old messages
        val ageMillis = System.currentTimeMillis() - message.timestamp.toLong()
        val shouldSpeak = recentMessageTimeMillis > ageMillis
        if (!shouldSpeak) return

        // todo move FMC logic here

        // ensure engine is ready
        appScope.launch {
            isTtsReady.filter { it }.first()
            tts.speak(message.timestamp, message.message)
        }
    }

    // onboarding
    suspend fun loadOnboarding() =
        readPreference(booleanPreferencesKey(onboardingKey)) ?: false
    fun updateOnboarding(enabled: Boolean) =
        writePreference(booleanPreferencesKey(onboardingKey), enabled)

    // mindfulness quote
    suspend fun getMindfulnessQuote() = rapidApi.getMindfulnessQuote()
    suspend fun loadShowQuote() =
        readPreference(booleanPreferencesKey(isShowQuoteKey)) ?: true
    fun updateShowQuote(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isShowQuoteKey), enabled)

    // stream NQ
    suspend fun loadNQ() =
        readPreference(booleanPreferencesKey(isNQKey)) ?: true
    fun updateNQ(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            if (enabled) subscribeToTopic(nqTopic) else unsubscribeFromTopic(nqTopic)
        }
        writePreference(booleanPreferencesKey(isNQKey), enabled)
    }

    // full screen
    suspend fun loadFullScreen() =
        readPreference(booleanPreferencesKey(isFullScreenKey)) ?: false
    fun updateFullScreen(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isFullScreenKey), enabled)

    // dark mode
    suspend fun loadDarkMode(systemDefault: Boolean) =
        readPreference(booleanPreferencesKey(isDarkModeKey)) ?: systemDefault
    fun updateDarkMode(enabled: Boolean) =
        writePreference(booleanPreferencesKey(isDarkModeKey), enabled)

    init {

        // wait for system initialization of tts engine, takes a few seconds
        appScope.launch {
            isTtsInit.filter { it }.first()
            initTtsSettings()
            _isTtsReady.update { true }
        }

        // wait for firebase to initialize to ensure uid is valid
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val uid = auth.currentUser?.uid ?: return@addAuthStateListener
            firebaseDb.setUid(uid)
            newToken?.let { token -> writeNewToken(token) }
        }
    }

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

    fun signOut() =
        Firebase.auth.signOut()

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // firebase database (token)
    var newToken: String? = null
    fun onNewToken(token: String) { newToken = token }
    fun writeNewToken(token: String) {
        appScope.launch {
            FirebaseMessaging.getInstance().apply {
                if (loadNQ()) subscribeToTopic(nqTopic) else unsubscribeFromTopic(nqTopic)
                // todo if other streamLoad() sub/unsub ...
            }
        }
        firebaseDb.writeToken(token)
    }

    // preference data store
    suspend fun <T> readPreference(
        key: Preferences.Key<T>) : T? =
        dataStore.data.first()[key]

    fun <T> writePreference(
        key: Preferences.Key<T>,
        value: T) =
        appScope.launch {
            dataStore.edit { it[key] = value }
        }
}