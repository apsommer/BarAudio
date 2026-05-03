package com.sommerengineering.signalvoice

import com.google.firebase.Firebase
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
import com.sommerengineering.signalvoice.uitls.esStream
import com.sommerengineering.signalvoice.uitls.gcStream
import com.sommerengineering.signalvoice.uitls.nqStream
import com.sommerengineering.signalvoice.uitls.siStream
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
    private val sessionManager: SessionManager,
    private val tts: TextToSpeechImpl,
    private val roomDb: RoomImpl,
    private val firebaseDb: FirebaseDatabaseImpl,
    private val prefs: PreferenceStore
) {

    // room database
    val messages = roomDb.messages
    fun addMessage(message: Message) =
        appScope.launch { roomDb.addMessage(message) }

    // firebase database
    suspend fun hydrateStreamMessages() {

        if (loadZN()) roomDb.replaceStreamMessages(
            znStream,
            firebaseDb.fetchStreamMessages(znStream)
        )
        if (loadNQ()) roomDb.replaceStreamMessages(
            nqStream,
            firebaseDb.fetchStreamMessages(nqStream)
        )
        if (loadBTC()) roomDb.replaceStreamMessages(
            btcStream,
            firebaseDb.fetchStreamMessages(btcStream)
        )
        if (loadES()) roomDb.replaceStreamMessages(
            esStream,
            firebaseDb.fetchStreamMessages(esStream)
        )
        if (loadGC()) roomDb.replaceStreamMessages(
            gcStream,
            firebaseDb.fetchStreamMessages(gcStream)
        )
        if (loadSI()) roomDb.replaceStreamMessages(
            siStream,
            firebaseDb.fetchStreamMessages(siStream)
        )
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
            appScope.launch { prefs.write(VOICE_NAME, value.name) }
        }

    // speed
    var speed
        get() = tts.speed
        set(value) {
            val roundedSpeed = ((value * 10).roundToInt()).toFloat() / 10
            tts.speed = roundedSpeed
            appScope.launch { prefs.write(SPEED, roundedSpeed) }
        }

    // pitch
    var pitch
        get() = tts.pitch
        set(value) {
            val roundedPitch = ((value * 10).roundToInt()).toFloat() / 10
            tts.pitch = roundedPitch
            appScope.launch { prefs.write(PITCH, roundedPitch) }
        }

    // listening
    private val _isListening = MutableStateFlow(true)
    val isListening = _isListening.asStateFlow()

    fun setListening(enabled: Boolean) {
        tts.isMute = !enabled
        if (!enabled && tts.isSpeaking()) { // stop any current speech
            tts.stop()
        }
        _isListening.value = enabled
        appScope.launch { prefs.write(LISTENING, enabled) }
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

        tts.speakQueued(message.timestamp, spokenText)
    }

    suspend fun speakPreview(text: String) {

        // ensure engine is ready
        isTtsReady.filter { it }.first()
        tts.speakImmediate(text)
    }

    // onboarding
    suspend fun loadOnboarding() =
        prefs.read(ONBOARDING) ?: false

    fun updateOnboarding(enabled: Boolean) =
        appScope.launch { prefs.write(ONBOARDING, enabled) }

    // user signal empty state
    suspend fun loadEmptyState() =
        prefs.read(EMPTY_STATE) ?: true

    fun updateEmptyState(enabled: Boolean) =
        appScope.launch { prefs.write(EMPTY_STATE, enabled) }

    // stream ZN
    suspend fun loadZN() =
        prefs.read(ZN) ?: true

    fun updateZN(enabled: Boolean) {
        syncStream(znStream, enabled)
        appScope.launch { prefs.write(ZN, enabled) }
    }

    // stream NQ
    suspend fun loadNQ() =
        prefs.read(NQ) ?: true

    fun updateNQ(enabled: Boolean) {
        syncStream(nqStream, enabled)
        appScope.launch { prefs.write(NQ, enabled) }
    }

    // stream BTC
    suspend fun loadBTC() =
        prefs.read(BTC) ?: true

    fun updateBTC(enabled: Boolean) {
        syncStream(btcStream, enabled)
        appScope.launch { prefs.write(BTC, enabled) }
    }

    // stream ES
    suspend fun loadES() =
        prefs.read(ES) ?: true

    fun updateES(enabled: Boolean) {
        syncStream(esStream, enabled)
        appScope.launch { prefs.write(ES, enabled) }
    }

    // stream GC
    suspend fun loadGC() =
        prefs.read(GC) ?: true

    fun updateGC(enabled: Boolean) {
        syncStream(gcStream, enabled)
        appScope.launch { prefs.write(GC, enabled) }
    }

    // stream SI
    suspend fun loadSI() =
        prefs.read(SI) ?: true

    fun updateSI(enabled: Boolean) {
        syncStream(siStream, enabled)
        appScope.launch { prefs.write(SI, enabled) }
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
            roomDb.replaceStreamMessages(stream, streamMessages)
            return@launch
        }

        // unsubscribe and remove from local database
        FirebaseMessaging.getInstance().unsubscribeFromTopic(stream)
        roomDb.removeStreamMessages(stream)
    }

    // feed mode
    suspend fun loadFeedMode(): FeedMode {
        val saved = prefs.read(FEED_MODE)
        return FeedMode.entries.firstOrNull { it.name == saved } ?: FeedMode.Linear
    }

    fun updateFeedMode(feedMode: FeedMode) =
        appScope.launch { prefs.write(FEED_MODE, feedMode.name) }

    // full screen
    suspend fun loadFullScreen() =
        prefs.read(FULLSCREEN) ?: false

    fun updateFullScreen(enabled: Boolean) =
        appScope.launch { prefs.write(FULLSCREEN, enabled) }

    suspend fun initTtsSettings() {

        tts.voice = prefs.read(VOICE_NAME)
            ?.let { preference -> voices.firstOrNull { it.name == preference } }
            ?: voices.firstOrNull { it.name == defaultVoice }
                    ?: voice
        tts.speed = prefs.read(SPEED) ?: 1f
        tts.pitch = prefs.read(PITCH) ?: 1f
        setListening(prefs.read(LISTENING) ?: true)
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

    // firebase database (token)
    var newToken: String? = null
    fun onNewToken(token: String) {
        newToken = token
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


    ////////////////////////////////////////////////////////////////////////////////////////////////

    init {

        // observe session state
        appScope.launch {
            sessionManager.session.collect {
                val uid = (it as? Session.Authenticated)?.uid
                firebaseDb.setUid(uid) // set uid from flow emission to avoid race
                if (it is Session.Authenticated) {
                    hydrateUserMessages() // cold start hydration of user messages
                }
                newToken?.let { token ->
                    writeNewToken(token) // write new token, if needed
                    newToken = null
                }
            }
        }

        // initialize tts engine
        appScope.launch {
            isTtsInit.filter { it }.first() // ~10 millis
            stabilizeVoices() // ~500 millis todo remove this, solved a non-existent problem
            initTtsSettings()
            _isTtsReady.update { true }
        }
    }
}