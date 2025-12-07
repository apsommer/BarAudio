package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.QuoteState
import com.sommerengineering.baraudio.messages.tradingviewWhitelistIps
import com.sommerengineering.baraudio.messages.trendspiderWhitelistIp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

import kotlin.math.roundToInt

class MainViewModel(
    val tts: TextToSpeechImpl
) : ViewModel() {

    private val rapidApi : RapidApi by inject(RapidApi::class.java)
    var quoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    
    init {

        // init tts engine, takes a few seconds ...
        viewModelScope.launch(Dispatchers.Main) {
            tts.isInit
                .onEach { if (it) initTtsSettings() }
                .collect()
        }

        // init network call for mindfulness quote
        viewModelScope.launch(Dispatchers.IO) {
            getMindfulnessQuote()
        }
    }

    suspend fun getMindfulnessQuote() {

        quoteState.value = QuoteState.Loading
        try { quoteState.value = QuoteState.Success(rapidApi.getQuote()) }
        catch (e: Exception) { quoteState.value = QuoteState.Error(e.message) }
    }

    private fun initTtsSettings() {

        voices.addAll(
            tts.getVoices()
                .toList()
                .sortedBy { it.locale.displayName })

        initBeautifulVoiceNames()

        voiceDescription =
            beautifyVoiceName(
                tts.voice.value.name)

        speedDescription =
            tts.speed.toString()

        pitchDescription =
            tts.pitch.toString()

        queueDescription =
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription

        // todo remove this line on resume subscription requirement 310125
        billing.billingState.value = BillingState.Subscribed
    }

    // webhook
    fun saveToWebhookClipboard(
        context: Context,
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
    
    // voice
    val voices = mutableListOf<Voice>()
    var voiceDescription by mutableStateOf("")
    private val beautifulVoiceNames = hashMapOf<String, String>()

    fun setVoice(
        context: Context,
        voice: Voice) {

        tts.voice.value = voice
        writeToDataStore(context, voiceKey, voice.name)
        voiceDescription = beautifyVoiceName(voice.name)
        speakLastMessage()
    }

    private fun initBeautifulVoiceNames() {

        // group voices by combined language/country
        val groupedVoices = voices
            .groupBy {
                it.locale.displayName
            }

        // add roman numeral to display name
        groupedVoices.keys
            .forEach { languageCountryGroup ->

                val voices =
                    groupedVoices[languageCountryGroup]
                        ?: return@forEach

                voices
                    .forEachIndexed { i, voice ->
                        beautifulVoiceNames[voice.name] = enumerateVoices(voice, i)
                    }
            }
    }

    fun beautifyVoiceName(name: String) =
        beautifulVoiceNames[name] ?: ""

    private fun enumerateVoices(
        voice: Voice,
        number: Int): String {

        val displayName = voice.locale.displayName
        var romanNumeral = "I"

        if (number == 1) romanNumeral = "II"
        if (number == 2) romanNumeral = "III"
        if (number == 3) romanNumeral = "IV"
        if (number == 4) romanNumeral = "V"
        if (number == 5) romanNumeral = "VI"
        if (number == 6) romanNumeral = "VII"
        if (number == 7) romanNumeral = "VIII"
        if (number == 8) romanNumeral = "IX"
        if (number == 9) romanNumeral = "X"
        if (number == 10) romanNumeral = "XI"
        if (number == 11) romanNumeral = "XII"
        if (number == 12) romanNumeral = "XIII"
        if (number == 13) romanNumeral = "XIV"
        if (number == 14) romanNumeral = "XV"
        if (number == 15) romanNumeral = "XVI"
        if (number == 16) romanNumeral = "XVII"
        if (number == 17) romanNumeral = "XVIII"
        if (number == 18) romanNumeral = "XIX"
        if (number == 19) romanNumeral = "XX"

        return "$displayName • Voice $romanNumeral"
    }

    fun getVoiceIndex() =
        voices.indexOf(
            voices.find {
                it == tts.voice.value })

    // speed
    var speedDescription by mutableStateOf("")

    fun getSpeed() =
        tts.speed
    
    fun setSpeed(
        context: Context,
        rawSpeed: Float) {

        // round to nearest tenth
        val selectedSpeed = ((rawSpeed * 10).roundToInt()).toFloat() / 10

        tts.speed = selectedSpeed
        writeToDataStore(context, speedKey, selectedSpeed.toString())
        speedDescription = selectedSpeed.toString()
    }

    // pitch
    var pitchDescription by mutableStateOf("")

    fun getPitch() =
        tts.pitch
    
    fun setPitch(
        context: Context,
        rawPitch: Float) {

        // round to nearest tenth
        val selectedPitch = ((rawPitch * 10).roundToInt()).toFloat() / 10

        tts.pitch = selectedPitch
        writeToDataStore(context, pitchKey, selectedPitch.toString())
        pitchDescription = selectedPitch.toString()
    }

    // queue behavior
    var queueDescription by mutableStateOf("")

    fun isQueueAdd() =
        tts.isQueueAdd
    
    fun setIsQueueAdd(
        context: Context,
        isChecked: Boolean) {

        tts.isQueueAdd = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())

        queueDescription =
            if (tts.isQueueAdd) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }

    // dark mode
    var isDarkMode by mutableStateOf(true)
    var isSystemInDarkTheme = false
    var uiModeDescription by mutableStateOf("")

    fun setUiMode(
        context: Context) {

        isDarkMode =
            if (Firebase.auth.currentUser == null) isSystemInDarkTheme
            else readFromDataStore(context, isDarkModeKey)?.toBooleanStrictOrNull() ?: true

        uiModeDescription =
            if (isDarkMode) uiModeDarkDescription
            else uiModeLightDescription
    }

    fun setIsDarkMode(
        context: Context,
        isChecked: Boolean) {

        isDarkMode = isChecked
        writeToDataStore(context, isDarkModeKey, isChecked.toString())

        uiModeDescription =
            if (isDarkMode) uiModeDarkDescription
            else uiModeLightDescription
    }

    // fullscreen
    var isFullScreen by mutableStateOf(false)
    var fullScreenDescription by mutableStateOf("")

    fun setFullScreen(
        context: Context,
        isChecked: Boolean) {

        isFullScreen = isChecked
        writeToDataStore(context, isFullScreenKey, isChecked.toString())

        fullScreenDescription =
            if (isFullScreen) fullScreenOnDescription
            else fullScreenOffDescription

        // expand or collapse layout
        toggleFullScreen(context, isFullScreen)
    }

    fun toggleFullScreen(
        context: Context,
        isFullScreen: Boolean) {

        val windowInsetsController = (context as MainActivity).windowInsetsController

        // expand
        if (isFullScreen) {
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        // collapse
        else { windowInsetsController.show(WindowInsetsCompat.Type.systemBars()) }
    }

    // billing client, purchase subscription flow ui triggered by mute button
    private lateinit var billing: BillingClientImpl

    fun initBilling(
        billingClientImpl: BillingClientImpl) {

        // initialize connection to google play
        billing = billingClientImpl
        billing.connect()

        val context = billing.context

        // listen to subscription status
        CoroutineScope(Dispatchers.Main).launch {
            billing.billingState
                .onEach {
                    when (it) {

                        // show spinner
                        BillingState.Loading -> {
                            shouldShowSpinner = true
                        }

                        BillingState.NewSubscription -> {
                            setMute(context, false)
                            this@MainViewModel.speakLastMessage()
                        }

                        BillingState.Subscribed -> {
                            tts.volume = readFromDataStore(context, volumeKey)?.toFloat() ?: 0f // todo revert this to "1f" on resume subscription requirement 310125
                            isMute = tts.volume == 0f
                        }

                        BillingState.Unsubscribed, BillingState.Error -> { }
                    }

                    // hide spinner
                    if (it != BillingState.Loading) {
                        shouldShowSpinner = false
                    }
                }
                .collect()
        }
    }

    // mute
    var shouldShowSpinner by mutableStateOf(false)
    var isMute by mutableStateOf(true)

    fun toggleMute(
        context: Context) {

        // todo disable subscription requirement 310125
        // unmute only allowed for paid user
//        val isUserPaid =
//            billing.billingState.value == BillingState.NewSubscription ||
//            billing.billingState.value == BillingState.Subscribed
//
//        if (isMute && !isUserPaid) {
//
//            billing.launchBillingFlowUi(context)
//            return
//        }

        // todo remove this block on resume subscription requirement 310125
        if (isFirstLaunch) {
            speakLastMessage()
            isFirstLaunch = false
            writeToDataStore(context, isFirstLaunchKey, isFirstLaunch.toString())
        }

        setMute(context, !isMute)
    }

    private fun setMute(
        context: Context,
        newMute: Boolean) {

        isMute = newMute

        if (isMute) { tts.volume = 0f }
        else { tts.volume = 1f }

        if (isMute && tts.isSpeaking()) { tts.stop() }

        writeToDataStore(context, volumeKey, tts.volume.toString())
    }

    val messages = mutableStateListOf<Message>()
    fun speakLastMessage() {

        val lastMessage =
            if (messages.isEmpty()) defaultMessage
            else messages.last().message

        tts.speak(
            timestamp = "",
            message = lastMessage,
            isForceVolume = true)
    }

    // images //////////////////////////////////////////////////////////////////////////////////////

    fun getGitHubImageId() =
        if (isDarkMode) R.drawable.github_light
        else R.drawable.github_dark

    fun getBackgroundId() =
        if (isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

    @Composable
    fun getFabIconColor() =
        if (isMute) MaterialTheme.colorScheme.outline
        else MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun getFabBackgroundColor() =
        if (isMute) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer

    fun getOriginImageId(
        origin: String): Int? {

        return when (origin) {
            in tradingviewWhitelistIps -> {
                if (isDarkMode) R.drawable.tradingview_light
                else R.drawable.tradingview_dark
            }
            trendspiderWhitelistIp -> R.drawable.trendspider
            com.sommerengineering.baraudio.messages.error -> R.drawable.error
            else -> {
                if (BuildConfig.BUILD_TYPE == buildTypeDebug) R.drawable.insomnia
                else null
            }
        }
    }
}
