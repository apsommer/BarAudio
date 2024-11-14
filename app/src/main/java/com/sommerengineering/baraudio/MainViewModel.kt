package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt


class MainViewModel(
    private val tts: TextToSpeechImpl,
    private val repository: Repository
) : ViewModel() {

    var isQueueFlush = MutableStateFlow(false)
    var speed = MutableStateFlow(1f)
    var pitch = MutableStateFlow(1f)

    // init preferences datastore
    private val uid = Firebase.auth.currentUser?.uid
    val tokenKey by lazy { uid + tokenBaseKey }
    val isQueueFlushKey by lazy { uid + isQueueFlushBaseKey }
    val speedKey by lazy { uid + speedBaseKey }
    val pitchKey by lazy { uid + pitchBaseKey }

    val voiceDescription by lazy { mutableStateOf("English - austrialian accent - male") }
    val speedDescription by lazy { mutableStateOf(speed.value.toString()) }
    val pitchDescription by lazy { mutableStateOf(pitch.value.toString()) }
    val queueBehaviorDescription by lazy { mutableStateOf(getQueueSettingDescription()) }

    fun initConfig(context: Context) {

        isQueueFlush.value = readFromDataStore(context, isQueueFlushKey).toBoolean()
        speed.value = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch.value = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f

        logMessage("Text-to-speech config initialized")
        logMessage(isQueueFlush.value.toString())
        logMessage(speed.value.toString())
        logMessage(pitch.value.toString())
        logMessage(this.hashCode().toString())
    }

    // webhook
    val webhookUrl by lazy { webhookBaseUrl + Firebase.auth.currentUser?.uid }
    fun saveToClipboard(context: Context) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)
    }

    fun getVoices() =
        tts.getVoices().toList()

    fun getSpeed() = speed.value
    fun setSpeed(
        context: Context,
        rawSpeed: Float) {

        // round to nearest tenth
        val selectedSpeed = ((rawSpeed * 10).roundToInt()).toFloat() / 10

        speed.value = selectedSpeed
        writeToDataStore(context, speedKey, selectedSpeed.toString())
        speedDescription.value = selectedSpeed.toString()
    }

    // pitch
    fun getPitch() = pitch.value
    fun setPitch(
        context: Context,
        rawPitch: Float) {

        // round to nearest tenth
        val selectedPitch = ((rawPitch * 10).roundToInt()).toFloat() / 10

        pitch.value = selectedPitch
        pitchDescription.value = selectedPitch.toString()
        writeToDataStore(context, pitchKey, selectedPitch.toString())
    }

    // queue behavior
    fun setIsQueueFlush(
        context: Context,
        isChecked: Boolean) {

        isQueueFlush.value = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())
        setQueueSettingDescription()
    }

    fun getQueueSettingDescription() =
        if (isQueueFlush.value) queueBehaviorFlushDescription
        else queueBehaviorAddDescription

    fun setQueueSettingDescription() {

        val description =
            if (isQueueFlush.value) queueBehaviorFlushDescription
            else queueBehaviorAddDescription

        queueBehaviorDescription.value = description
    }

    fun readFromDataStore(
        context: Context,
        key: String): String? {

        return runBlocking {
            context.dataStore.data.map {
                it[stringPreferencesKey(key)]
            }.first()
        }
    }

    fun writeToDataStore(
        context: Context,
        key: String,
        value: String) {

        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }

        if (key == tokenKey) logMessage("New token: $value")
    }
}
