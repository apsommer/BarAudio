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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.hilt.TextToSpeechImpl
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.hilt.writeWhitelistToDatabase
import com.sommerengineering.baraudio.messages.MindfulnessQuoteState
import com.sommerengineering.baraudio.messages.tradingviewWhitelistIps
import com.sommerengineering.baraudio.messages.trendspiderWhitelistIp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repo: MainRepository,
    val tts: TextToSpeechImpl,
    val credentialManager: CredentialManager,
    @ApplicationContext val context: Context
) : ViewModel() {

    private var _mindfulnessQuoteState: MutableStateFlow<MindfulnessQuoteState> = MutableStateFlow(MindfulnessQuoteState.Idle)
    val mindfulnessQuoteState = _mindfulnessQuoteState.asStateFlow()

    private var _showQuote by mutableStateOf(true)
    val showQuote get() = _showQuote

    private var _isFuturesWebhooks by mutableStateOf(true)
    val isFuturesWebhooks get() = _isFuturesWebhooks

    init {

        // observe initialization of tts engine, takes a few seconds
        viewModelScope.launch(Dispatchers.Main) {
            repo.observeTtsInit {
                repo.initTtsSettings()
            }.collect()
        }

        // request mindfulness quote from network
        viewModelScope.launch(Dispatchers.IO) {
            _mindfulnessQuoteState.value = MindfulnessQuoteState.Loading
            try { _mindfulnessQuoteState.value = MindfulnessQuoteState.Success(repo.getMindfulnessQuote()) }
            catch (e: Exception) { _mindfulnessQuoteState.value = MindfulnessQuoteState.Error(e.message) }
        }

        // mindfulness quote
        val showQuote = readFromDataStore(context, showQuoteKey)?.toBooleanStrictOrNull() ?: true
        showQuote(showQuote)

        // futures webhooks
        val isFuturesWebhooksKey = readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true
        setFuturesWebhooks(isFuturesWebhooksKey)
    }

    val voices = repo.voices
    val messages = repo.messages
    val voiceDescription = repo.voiceDescription
    var speedDescription = repo.speedDescription
    var pitchDescription = repo.pitchDescription
    var queueDescription = repo.queueDescription
    var isMute = repo.isMute

    fun setVoice(voice: Voice) = repo.setVoice(voice)
    fun beautifyVoiceName(name: String) = repo.beautifyVoiceName(name)
    fun getVoiceIndex() = repo.getVoiceIndex()

    fun getSpeed() = repo.getSpeed()
    fun setSpeed(rawSpeed: Float) { repo.setSpeed(rawSpeed) }

    fun getPitch() = repo.getPitch()
    fun setPitch(rawPitch: Float) = repo.setPitch(rawPitch)

    fun isQueueAdd() = repo.isQueueAdd()
    fun setIsQueueAdd(isChecked: Boolean) = repo.setIsQueueAdd(isChecked)

    fun toggleMute() = repo.toggleMute()
    fun speakLastMessage() = repo.speakLastMessage()

















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
    fun showQuote(
        isChecked: Boolean) {

        _showQuote = isChecked
        writeToDataStore(context, showQuoteKey, isChecked.toString())
    }

    // futures
    fun setFuturesWebhooks(
        isChecked: Boolean) {

        _isFuturesWebhooks = isChecked
        writeToDataStore(context, isFuturesWebhooksKey, isChecked.toString())
        writeWhitelistToDatabase(_isFuturesWebhooks)
    }

    // images //////////////////////////////////////////////////////////////////////////////////////

    fun getGitHubImageId() =
        if (isDarkMode) R.drawable.github_light
        else R.drawable.github_dark

    fun getBackgroundId() =
        if (isDarkMode) R.drawable.background_skyline_dark
        else R.drawable.background_skyline

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
