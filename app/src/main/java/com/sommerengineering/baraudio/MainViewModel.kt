package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.messages.tradingviewWhitelistIps
import com.sommerengineering.baraudio.messages.trendspiderWhitelistIp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.roundToInt

class MainViewModel(
    val tts: TextToSpeechImpl
) : ViewModel() {
    
    init {
        CoroutineScope(Dispatchers.Main).launch {
            tts.isInit
                .onEach { if (it) initTtsSettings() }
                .collect()
        }
    }

    lateinit var voices: List<Voice>
    var voiceDescription by mutableStateOf("")
    var speedDescription by mutableStateOf("")
    var pitchDescription by mutableStateOf("")
    var queueDescription by mutableStateOf("")
    
    private fun initTtsSettings() {

        voices =
            tts.getVoices()
                .toList()
                .sortedBy { it.locale.displayName }

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

    private val beautifulVoiceNames =
        hashMapOf<String, String>()

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

    fun speakLastMessage() {

        getDatabaseReference(messagesNode)
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    var lastMessage = defaultMessage

                    val children = snapshot.children
                    if (!children.none()) {

                        // parse json
                        val json = JSONObject(children.first().value.toString())
                        val jsonMessage = json.getString(messageKey)
                        if (jsonMessage.isNotEmpty()) lastMessage = jsonMessage
                    }

                    tts.speak(
                        timestamp = "",
                        message = lastMessage,
                        isForceVolume = true)
                }

                override fun onCancelled(error: DatabaseError) { }
            })
    }

    // queue behavior
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
    val isDarkMode = mutableStateOf(true)
    var isSystemInDarkTheme = false

    val uiModeDescription by lazy {
        mutableStateOf(
            if (isDarkMode.value) uiModeDarkDescription
            else uiModeLightDescription)
    }

    fun setUiMode(
        context: Context) {

        isDarkMode.value =
            if (Firebase.auth.currentUser == null) isSystemInDarkTheme
            else readFromDataStore(context, isDarkModeKey)?.toBooleanStrictOrNull() ?: true
    }

    fun setIsDarkMode(
        context: Context,
        isChecked: Boolean) {

        isDarkMode.value = isChecked
        writeToDataStore(context, isDarkModeKey, isChecked.toString())

        uiModeDescription.value =
            if (isDarkMode.value) uiModeDarkDescription
            else uiModeLightDescription
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
                            tts.volume = readFromDataStore(context, volumeKey)?.toFloat() ?: 1f
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
    // todo duplication of mutableStates through VM, use .collectAsState in composables per docs
    var shouldShowSpinner by mutableStateOf(false)
    var isMute by mutableStateOf(true)

    fun toggleMute(
        context: Context) {

        // unmute only allowed for paid user
        val isUserPaid =
            billing.billingState.value == BillingState.NewSubscription ||
            billing.billingState.value == BillingState.Subscribed

        if (isMute && !isUserPaid) {

            billing.launchBillingFlowUi(context)
            return
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

    // images //////////////////////////////////////////////////////////////////////////////////////

    fun getGitHubImageId() =
        if (isDarkMode.value) R.drawable.github_light
        else R.drawable.github_dark

    fun getBackgroundId() =
        if (isDarkMode.value) R.drawable.background_skyline_dark
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
                if (isDarkMode.value) R.drawable.tradingview_light
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
