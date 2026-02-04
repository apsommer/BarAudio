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
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.hilt.BillingClientImpl
import com.sommerengineering.baraudio.hilt.BillingState
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.MindfulnessQuoteState
import com.sommerengineering.baraudio.messages.tradingviewWhitelistIps
import com.sommerengineering.baraudio.messages.trendspiderWhitelistIp
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: MainRepository,
    val tts: TextToSpeechImpl,
    val credentialManager: CredentialManager,
    @ApplicationContext val context: Context
) : ViewModel() {

    // mindfulness quote
    private var _mindfulnessQuoteState: MutableStateFlow<MindfulnessQuoteState> = MutableStateFlow(MindfulnessQuoteState.Idle)
    val mindfulnessQuoteState = _mindfulnessQuoteState.asStateFlow()

    fun getMindfulnessQuote() {
        viewModelScope.launch(Dispatchers.IO) {
            _mindfulnessQuoteState.value = MindfulnessQuoteState.Loading
            try { _mindfulnessQuoteState.value = MindfulnessQuoteState.Success(repository.getMindfulnessQuote()) }
            catch (e: Exception) { _mindfulnessQuoteState.value = MindfulnessQuoteState.Error(e.message) }
        }
    }

    init {

        // init tts engine, takes a few seconds ...
        viewModelScope.launch(Dispatchers.Main) {
            tts.isInit
                .onEach { if (it) initTtsSettings() }
                .collect()
        }
    }

    // voice
    val voices = mutableListOf<Voice>()
    var voiceDescription by mutableStateOf("")
    private val beautifulVoiceNames = hashMapOf<String, String>()
    var speedDescription by mutableStateOf("")
    var pitchDescription by mutableStateOf("")
    var queueDescription by mutableStateOf("")

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

        // todo mute button ui must be faster
        tts.volume = readFromDataStore(context, volumeKey)?.toFloat() ?: 0f
        isMute = tts.volume == 0f
    }

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

    fun beautifyVoiceName(name: String) = beautifulVoiceNames[name] ?: ""

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

    // dark mode
    var isDarkMode by mutableStateOf(true)
    var isSystemInDarkTheme = false
    var uiModeDescription by mutableStateOf("")

    fun setUiMode() {

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
            if (isFullScreen) screenFullDescription
            else screenWindowedDescription

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

    // quote
    var showQuote by mutableStateOf(true)

    fun showQuote(
        context: Context,
        isChecked: Boolean) {

        showQuote = isChecked
        writeToDataStore(context, showQuoteKey, isChecked.toString())
    }

    // futures
    var isFuturesWebhooks by mutableStateOf(true)
    fun setFuturesWebhooks(
        context: Context,
        isChecked: Boolean) {

        isFuturesWebhooks = isChecked
        writeToDataStore(context, isFuturesWebhooksKey, isChecked.toString())
        writeWhitelistToDatabase(isFuturesWebhooks)
    }



    // mute
    var shouldShowSpinner by mutableStateOf(false)
    var isMute by mutableStateOf(false) // default unmuted

    fun toggleMute(
        context: Context) {

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
