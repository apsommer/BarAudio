package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import kotlin.math.roundToInt

class MainViewModel(
    val tts: TextToSpeechImpl,
    private val repository: Repository
) : ViewModel() {

    val voiceDescription by lazy { mutableStateOf(beautifyVoiceName(tts.voice.value.name)) }
    val speedDescription by lazy { mutableStateOf(tts.speed.value.toString()) }
    val pitchDescription by lazy { mutableStateOf(tts.pitch.value.toString()) }
    val queueBehaviorDescription by lazy { mutableStateOf(getQueueBehaviorDescription()) }
    val uiModeDescription by lazy { mutableStateOf(getUiModeDescription()) }

    // webhook
    val webhookUrl by lazy { webhookBaseUrl + Firebase.auth.currentUser?.uid }
    fun saveToClipboard(context: Context) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)
    }

    // voice
    val voices by lazy {
        tts.getVoices()
            .toList()
            .sortedBy { it.locale.displayName }
    }

    fun setVoice(
        context: Context,
        voice: Voice) {

        tts.voice.value = voice
        writeToDataStore(context, voiceKey, voice.name)
        voiceDescription.value = beautifyVoiceName(voice.name)
    }

    fun getVoiceIndex() =
        voices.indexOf(
            voices.find {
                it == tts.voice.value })

    fun speakLastMessage() {

        // todo get from local cache
        //  temporarily get from db

        val uid = Firebase.auth.currentUser?.uid ?: unauthenticatedUser
        val db = Firebase.database(databaseUrl)
        val dbRef = db.getReference(messages).child(uid)

        dbRef.limitToFirst(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var lastMessage = defaultMessage

                val children = snapshot.children
                if (!children.none()) {

                    // parse json
                    val json = JSONObject(children.first().value.toString())
                    val jsonMessage = json.getString(message)
                    if (jsonMessage.isNotEmpty()) lastMessage = jsonMessage
                }

                tts.speak("", lastMessage)
            }

            override fun onCancelled(error: DatabaseError) { }
        })
    }

    // speed
    fun getSpeed() =
        tts.speed.value

    fun setSpeed(
        context: Context,
        rawSpeed: Float) {

        // round to nearest tenth
        val selectedSpeed = ((rawSpeed * 10).roundToInt()).toFloat() / 10

        tts.speed.value = selectedSpeed
        writeToDataStore(context, speedKey, selectedSpeed.toString())
        speedDescription.value = selectedSpeed.toString()
    }

    // pitch
    fun getPitch() =
        tts.pitch.value

    fun setPitch(
        context: Context,
        rawPitch: Float) {

        // round to nearest tenth
        val selectedPitch = ((rawPitch * 10).roundToInt()).toFloat() / 10

        tts.pitch.value = selectedPitch
        pitchDescription.value = selectedPitch.toString()
        writeToDataStore(context, pitchKey, selectedPitch.toString())
    }

    // queue behavior
    var isQueueAdd =
        tts.isQueueAdd

    fun setIsQueueAdd(
        context: Context,
        isChecked: Boolean) {

        tts.isQueueAdd.value = isChecked
        writeToDataStore(context, isQueueAddKey, isChecked.toString())
        setQueueBehaviorDescription()
    }

    fun getQueueBehaviorDescription() =
        if (tts.isQueueAdd.value) queueBehaviorAddDescription
        else queueBehaviorFlushDescription

    fun setQueueBehaviorDescription() {
        queueBehaviorDescription.value =
            if (tts.isQueueAdd.value) queueBehaviorAddDescription
            else queueBehaviorFlushDescription
    }

    val isDarkMode = mutableStateOf(true)
    fun initDarkMode(
        context: Context) {

        isDarkMode.value = readFromDataStore(context, isDarkModeKey).toBoolean()
    }

    fun setIsDarkMode(
        context: Context,
        isChecked: Boolean) {

        isDarkMode.value = isChecked
        writeToDataStore(context, isDarkModeKey, isChecked.toString())
        setUiModeDescription()
    }

    fun getUiModeDescription() =
        if (isDarkMode.value) uiModeDarkDescription
        else uiModeLightDescription

    fun setUiModeDescription() {
        uiModeDescription.value =
            if (isDarkMode.value) uiModeDarkDescription
            else uiModeLightDescription
    }

    fun beautifyVoiceName(name: String) =
        voiceNameMap[name] ?: ""

    val voiceNameMap by lazy {

        val map = HashMap<String, String>()

        // group voices by language/country
        val grouped = voices
            .groupBy { it.locale.displayName }

        // iterate through groups adding roman numerals to display name
        grouped.keys.forEach { languageCountry ->
            val voices = grouped[languageCountry]
            voices?.forEachIndexed { i, voice ->
                map[voice.name] = enumerateVoices(voice, i)
            }
        }

        map
    }

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

        return "$displayName \u2022 Voice $romanNumeral"
    }

    fun getGoogleImageId() =
        if (isDarkMode.value) R.drawable.google_dark
        else R.drawable.google_light

    fun getGitHubImageId() =
        if (isDarkMode.value) R.drawable.github_light
        else R.drawable.github_dark
}
