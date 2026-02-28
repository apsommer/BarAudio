package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
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
import com.sommerengineering.baraudio.messages.FeedMode
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.room.RoomImpl
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.speak.TextToSpeechImpl
import com.sommerengineering.baraudio.uitls.defaultVoice
import com.sommerengineering.baraudio.uitls.feedModeKey
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.isDarkModeKey
import com.sommerengineering.baraudio.uitls.isFullScreenKey
import com.sommerengineering.baraudio.uitls.isGCKey
import com.sommerengineering.baraudio.uitls.isMuteKey
import com.sommerengineering.baraudio.uitls.isNQKey
import com.sommerengineering.baraudio.uitls.isQueueAddKey
import com.sommerengineering.baraudio.uitls.nqStream
import com.sommerengineering.baraudio.uitls.onboardingKey
import com.sommerengineering.baraudio.uitls.pitchKey
import com.sommerengineering.baraudio.uitls.speedKey
import com.sommerengineering.baraudio.uitls.voiceNameKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class MainRepository @Inject constructor(
    @ApplicationContext val context: Context,
    @ApplicationScope val appScope: CoroutineScope,
    val tts: TextToSpeechImpl,
    val roomDb: RoomImpl,
    val firebaseDb: FirebaseDatabaseImpl,
    val dataStore: DataStore<Preferences>) {

    // room database
    val messages = roomDb.messages
        .stateIn(appScope, SharingStarted.Eagerly, emptyList())
    fun addMessage(message: Message) =
        appScope.launch { roomDb.addMessage(message) }

    // firebase database
    suspend fun hydrateMessages() {

        val messages = mutableListOf<Message>()

        // streams
        if (loadNQ()) messages.addAll(firebaseDb.fetchStreamMessages(nqStream))
        if (loadGC()) messages.addAll(firebaseDb.fetchStreamMessages(gcStream))

        // user specific
        messages.addAll(firebaseDb.fetchUserMessages())

        // update local room database
        roomDb.addMessages(messages)
    }

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
    suspend fun speakMessage(message: Message) {

        // ensure engine is ready
        isTtsReady.filter { it }.first()

        // prepend name of stream, if needed
        val origin = resolveMessageOrigin(message)
        val spokenText =
            if (origin is MessageOrigin.BroadcastStream) { "${origin.asset.spokenName}. ${message.message}" }
            else { message.message }

        tts.speak(message.timestamp, spokenText)
    }

    // onboarding
    suspend fun loadOnboarding() =
        readPreference(booleanPreferencesKey(onboardingKey)) ?: false
    fun updateOnboarding(enabled: Boolean) =
        writePreference(booleanPreferencesKey(onboardingKey), enabled)

    // stream NQ
    suspend fun loadNQ() =
        readPreference(booleanPreferencesKey(isNQKey)) ?: true
    fun updateNQ(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            if (enabled) subscribeToTopic(nqStream) else unsubscribeFromTopic(nqStream)
        }
        writePreference(booleanPreferencesKey(isNQKey), enabled)
    }

    // stream GC
    suspend fun loadGC() =
        readPreference(booleanPreferencesKey(isGCKey)) ?: true
    fun updateGC(enabled: Boolean) {
        FirebaseMessaging.getInstance().apply {
            if (enabled) subscribeToTopic(gcStream) else unsubscribeFromTopic(gcStream)
        }
        writePreference(booleanPreferencesKey(isGCKey), enabled)
    }

    // feed mode
    suspend fun loadFeedMode(): FeedMode {
        val saved = readPreference(stringPreferencesKey(feedModeKey))
        return FeedMode.entries.firstOrNull { it.name == saved } ?: FeedMode.Linear
    }
    fun updateFeedMode(feedMode: FeedMode) =
        writePreference(stringPreferencesKey(feedModeKey), feedMode.name)

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

        appScope.launch {

            // wait for system initialization of tts engine, takes a few milliseconds
            isTtsInit.filter { it }.first()

            // ensure voices are stable, can take 500 milliseconds on slow devices
            var voiceCount = -1
            var stablePasses = 0
            var attempts = 0
            val maxAttempts = 40
            while (true) {

                // query engine state
                val voices = tts.voices
                val currentVoiceCount = voices.size

                // check size of voices and their attributes
                val isSizeStable = currentVoiceCount > 0 && currentVoiceCount == voiceCount
                val areVoicesStable = voices.all { it.name != null && it.locale != null }

                if (isSizeStable && areVoicesStable) {
                    stablePasses ++
                    if (stablePasses > 3) break // size and voices are stable, finish
                } else { stablePasses = 0 }

                // fail-safe exit
                // todo if this fail safe occurs tts engine is unusable, entire app will not function
                //  surface this to user in the existing AllowNotificationBottomBar
                attempts ++
                if (attempts > maxAttempts) break

                // voices unstable, try again
                voiceCount = currentVoiceCount
                delay(50)
            }

            // finish tts engine with store preferences
            initTtsSettings()
            _isTtsReady.update { true }
        }

        // wait for firebase to initialize to ensure uid is valid
        FirebaseAuth.getInstance().addAuthStateListener { auth ->

            val uid = auth.currentUser?.uid ?: return@addAuthStateListener
            firebaseDb.setUid(uid)

            // write new token to firebase database, if needed
            newToken?.let { token -> writeNewToken(token) }

            // cold start hydration sync of firebase to local room database
            appScope.launch { hydrateMessages() }
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
                if (loadNQ()) subscribeToTopic(nqStream) else unsubscribeFromTopic(nqStream)
                if (loadGC()) subscribeToTopic(gcStream) else unsubscribeFromTopic(gcStream)
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