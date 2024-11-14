package com.sommerengineering.baraudio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.speech.tts.Voice
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

    var voiceDescription = MutableStateFlow("")
    var queueBehaviorDescription = MutableStateFlow("")
    var speedDescription = MutableStateFlow("")
    var pitchDescription = MutableStateFlow("")

    // init preferences datastore
    private val uid = Firebase.auth.currentUser?.uid
    val tokenKey by lazy { uid + tokenBaseKey }
    val isQueueFlushKey by lazy { uid + isQueueFlushBaseKey }
    val speedKey by lazy { uid + speedBaseKey }
    val pitchKey by lazy { uid + pitchBaseKey }

    fun initSettings(context: Context) {

        voiceDescription.value = "English - austrialian accent - male"

        isQueueFlush.value = readFromDataStore(context, isQueueFlushKey).toBoolean()
        speed.value = readFromDataStore(context, speedKey)?.toFloat() ?: 1f
        pitch.value = readFromDataStore(context, pitchKey)?.toFloat() ?: 1f

        setQueueSettingDescription(context)
        speedDescription.value = speed.value.toString()
        pitchDescription.value = pitch.value.toString()
    }

    // webhook
    val webhookUrl by lazy { webhookBaseUrl + Firebase.auth.currentUser?.uid }
    fun saveToClipboard(context: Context) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)
    }

    fun getVoices() = tts.getVoices().toList()

    fun getSpeed() = speed.value
    fun setSpeed(
        context: Context,
        rawSpeed: Float) {

        // round to nearest tenth
        speed.value = ((rawSpeed * 10).roundToInt()).toFloat() / 10
        writeToDataStore(context, speedKey, speed.value.toString())
        speedDescription.value = speed.value.toString()
    }

    // pitch
    fun getPitch() = pitch.value
    fun setPitch(
        context: Context,
        rawPitch: Float) {

        // round to nearest tenth
        pitch.value = ((rawPitch * 10).roundToInt()).toFloat() / 10
        writeToDataStore(context, pitchKey, pitch.value.toString())
        pitchDescription.value = pitch.value.toString()
    }

    // queue behavior
    fun setIsQueueFlush(
        context: Context,
        isChecked: Boolean) {

        isQueueFlush.value = isChecked
        writeToDataStore(context, isQueueFlushKey, isChecked.toString())
        setQueueSettingDescription(context)
    }

    fun setQueueSettingDescription(
        context: Context) {

        val resId =
            if (isQueueFlush.value) R.string.queue_behavior_flush_description
            else R.string.queue_behavior_add_description

        queueBehaviorDescription.value = context.getString(resId)
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
