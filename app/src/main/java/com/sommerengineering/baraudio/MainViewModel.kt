package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class MainViewModel(
    private val tts: TextToSpeechImpl,
    private val repository: Repository
) : ViewModel() {

    val voiceDescription by lazy { mutableStateOf("English - austrialian accent - male") }
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
    fun getVoices() =
        tts.getVoices().toList()

    // speed
    fun getSpeed() = tts.speed.value
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
    fun getPitch() = tts.pitch.value
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
    var isQueueAdd = tts.isQueueAdd
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
    fun getIsDarkMode(context: Context): Boolean {
        isDarkMode.value = readFromDataStore(context, isDarkModeKey).toBoolean()
        return isDarkMode.value
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
}
