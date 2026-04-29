package com.sommerengineering.signalvoice

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
import com.sommerengineering.signalvoice.firebase.FirebaseDatabaseImpl
import com.sommerengineering.signalvoice.messages.FeedMode
import com.sommerengineering.signalvoice.room.RoomImpl
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.source.MessageOrigin
import com.sommerengineering.signalvoice.source.resolveMessageOrigin
import com.sommerengineering.signalvoice.speak.TextToSpeechImpl
import com.sommerengineering.signalvoice.uitls.btcStream
import com.sommerengineering.signalvoice.uitls.defaultVoice
import com.sommerengineering.signalvoice.uitls.emptyStateKey
import com.sommerengineering.signalvoice.uitls.esStream
import com.sommerengineering.signalvoice.uitls.feedModeKey
import com.sommerengineering.signalvoice.uitls.gcStream
import com.sommerengineering.signalvoice.uitls.isBTCKey
import com.sommerengineering.signalvoice.uitls.isESKey
import com.sommerengineering.signalvoice.uitls.isFullScreenKey
import com.sommerengineering.signalvoice.uitls.isGCKey
import com.sommerengineering.signalvoice.uitls.isMuteKey
import com.sommerengineering.signalvoice.uitls.isNQKey
import com.sommerengineering.signalvoice.uitls.isSIKey
import com.sommerengineering.signalvoice.uitls.isZNKey
import com.sommerengineering.signalvoice.uitls.nqStream
import com.sommerengineering.signalvoice.uitls.onboardingKey
import com.sommerengineering.signalvoice.uitls.pitchKey
import com.sommerengineering.signalvoice.uitls.siStream
import com.sommerengineering.signalvoice.uitls.speedKey
import com.sommerengineering.signalvoice.uitls.voiceNameKey
import com.sommerengineering.signalvoice.uitls.webhookBaseUrl
import com.sommerengineering.signalvoice.uitls.znStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class MainRepository @Inject constructor(
    @ApplicationScope val appScope: CoroutineScope,
    val tts: TextToSpeechImpl,
    val roomDb: RoomImpl,
    val firebaseDb: FirebaseDatabaseImpl,
    val dataStore: DataStore<Preferences>
) {

    // firebase authentication
    lateinit var webhookUrl: String

    // room database
    val messages = roomDb.messages
    fun addMessage(message: Message) =
        appScope.launch { roomDb.addMessage(message) }

    // firebase database
    suspend fun hydrateStreamMessages() {

        val messages = mutableListOf<Message>()

        // streams
        if (loadZN()) messages.addAll(firebaseDb.fetchStreamMessages(znStream))
        if (loadNQ()) messages.addAll(firebaseDb.fetchStreamMessages(nqStream))
        if (loadBTC()) messages.addAll(firebaseDb.fetchStreamMessages(btcStream))
        if (loadES()) messages.addAll(firebaseDb.fetchStreamMessages(esStream))
        if (loadGC()) messages.addAll(firebaseDb.fetchStreamMessages(gcStream))
        if (loadSI()) messages.addAll(firebaseDb.fetchStreamMessages(siStream))

        // sync local database: delete all, then add all
        roomDb.replaceMessages(messages)
    }

    suspend fun hydrateUserMessages() {
        val messages = firebaseDb.fetchUserMessages()
        roomDb.replaceUserMessages(messages)
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
            val roundedSpeed = ((value * 10).roundToInt()).toFloat() / 10
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

    // mute
    private val _isMute = MutableStateFlow(false)
    val isMute = _isMute.asStateFlow()

    fun setMute(isMute: Boolean) {
        tts.isMute = isMute
        if (isMute && tts.isSpeaking()) { // stop any current speech
            tts.stop()
        }
        _isMute.value = isMute
        writePreference(booleanPreferencesKey(isMuteKey), isMute)
    }

    suspend fun speakMessage(message: Message) {

        // ensure engine is ready
        isTtsReady.filter { it }.first()

        // prepend name of stream, if needed
        val origin = resolveMessageOrigin(message)
        val spokenText =
            if (origin is MessageOrigin.BroadcastStream) {
                "${origin.asset.spokenName}, ${message.message}"
            } else {
                message.message
            }

        tts.speak(message.timestamp, spokenText)
    }

    // onboarding
    suspend fun loadOnboarding() =
        readPreference(booleanPreferencesKey(onboardingKey)) ?: false

    fun updateOnboarding(enabled: Boolean) =
        writePreference(booleanPreferencesKey(onboardingKey), enabled)

    // user signal empty state
    suspend fun loadEmptyState() =
        readPreference(booleanPreferencesKey(emptyStateKey)) ?: true

    fun updateEmptyState(enabled: Boolean) =
        writePreference(booleanPreferencesKey(emptyStateKey), enabled)

    // stream ZN
    suspend fun loadZN() =
        readPreference(booleanPreferencesKey(isZNKey)) ?: true

    fun updateZN(enabled: Boolean) {
        syncStream(znStream, enabled)
        writePreference(booleanPreferencesKey(isZNKey), enabled)
    }

    // stream NQ
    suspend fun loadNQ() =
        readPreference(booleanPreferencesKey(isNQKey)) ?: true

    fun updateNQ(enabled: Boolean) {
        syncStream(nqStream, enabled)
        writePreference(booleanPreferencesKey(isNQKey), enabled)
    }

    // stream BTC
    suspend fun loadBTC() =
        readPreference(booleanPreferencesKey(isBTCKey)) ?: true

    fun updateBTC(enabled: Boolean) {
        syncStream(btcStream, enabled)
        writePreference(booleanPreferencesKey(isBTCKey), enabled)
    }

    // stream ES
    suspend fun loadES() =
        readPreference(booleanPreferencesKey(isESKey)) ?: true

    fun updateES(enabled: Boolean) {
        syncStream(esStream, enabled)
        writePreference(booleanPreferencesKey(isESKey), enabled)
    }

    // stream GC
    suspend fun loadGC() =
        readPreference(booleanPreferencesKey(isGCKey)) ?: true

    fun updateGC(enabled: Boolean) {
        syncStream(gcStream, enabled)
        writePreference(booleanPreferencesKey(isGCKey), enabled)
    }

    // stream SI
    suspend fun loadSI() =
        readPreference(booleanPreferencesKey(isSIKey)) ?: true

    fun updateSI(enabled: Boolean) {
        syncStream(siStream, enabled)
        writePreference(booleanPreferencesKey(isSIKey), enabled)
    }

    // sync stream with firebase/room
    fun syncStream(
        stream: String,
        enabled: Boolean
    ) = appScope.launch {

        // subscribe, fetch, and store messages
        if (enabled) {
            FirebaseMessaging.getInstance().subscribeToTopic(stream)
            val streamMessages = firebaseDb.fetchStreamMessages(stream)
            roomDb.replaceStream(stream, streamMessages)
            return@launch
        }

        // unsubscribe and remove from local database
        FirebaseMessaging.getInstance().unsubscribeFromTopic(stream)
        roomDb.removeStream(stream)
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

    suspend fun initTtsSettings() {

        tts.voice = readPreference(stringPreferencesKey(voiceNameKey))
            ?.let { preference -> voices.firstOrNull { it.name == preference } }
            ?: voices.firstOrNull { it.name == defaultVoice }
                    ?: voice
        tts.speed = readPreference(floatPreferencesKey(speedKey)) ?: 1f
        tts.pitch = readPreference(floatPreferencesKey(pitchKey)) ?: 1f
        setMute(readPreference(booleanPreferencesKey(isMuteKey)) ?: false)
    }

    fun signOut() {
        Firebase.auth.signOut()
        appScope.launch {
            roomDb.removeUserMessages()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun stabilizeVoices() {

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
                stablePasses++
                if (stablePasses > 3) break // size and voices are stable, finish
            } else {
                stablePasses = 0
            }

            // fail-safe exit
            // todo if this fail safe occurs tts engine is unusable, entire app will not function
            //  surface this to user in the existing AllowNotificationBottomBar
            attempts++
            if (attempts > maxAttempts) break

            // voices unstable, try again
            voiceCount = currentVoiceCount
            delay(50)
        }
    }

    private fun onAuth(
        auth: FirebaseAuth
    ) {

        val uid = auth.currentUser?.uid ?: return

        // associate uid with token, if needed
        newToken?.let { token -> writeNewToken(token) }
        webhookUrl = "$webhookBaseUrl$uid"

        // cold start hydration sync of user signals: firebase to local db
        firebaseDb.setUid(uid)
        appScope.launch { hydrateUserMessages() }
    }

    // firebase database (token)
    var newToken: String? = null
    fun onNewToken(token: String) {
        newToken = token
        writeNewToken(token)
    }

    fun writeNewToken(token: String) {
        appScope.launch {
            FirebaseMessaging.getInstance().apply {
                if (loadZN()) subscribeToTopic(znStream) else unsubscribeFromTopic(znStream)
                if (loadNQ()) subscribeToTopic(nqStream) else unsubscribeFromTopic(nqStream)
                if (loadBTC()) subscribeToTopic(btcStream) else unsubscribeFromTopic(btcStream)
                if (loadES()) subscribeToTopic(esStream) else unsubscribeFromTopic(esStream)
                if (loadGC()) subscribeToTopic(gcStream) else unsubscribeFromTopic(gcStream)
                if (loadSI()) subscribeToTopic(siStream) else unsubscribeFromTopic(siStream)
            }
        }
        firebaseDb.writeToken(token)
    }

    // preference data store
    suspend fun <T> readPreference(
        key: Preferences.Key<T>
    ): T? =
        dataStore.data.first()[key]

    fun <T> writePreference(
        key: Preferences.Key<T>,
        value: T
    ) =
        appScope.launch {
            dataStore.edit { it[key] = value }
        }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    init {

        appScope.launch {

            // wait for system initialization of tts engine, takes a few milliseconds
            isTtsInit.filter { it }.first()

            // ensure voices are stable, can take 500 milliseconds on slow devices
            stabilizeVoices()

            // finish tts engine with stored preferences
            initTtsSettings()
            _isTtsReady.update { true }
        }

        // wait for firebase to initialize to check auth state
        FirebaseAuth.getInstance().addAuthStateListener { onAuth(it) }
    }
}