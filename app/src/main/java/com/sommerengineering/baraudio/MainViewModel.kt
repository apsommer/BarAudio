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


class MainViewModel(
    private val tts: TextToSpeechImpl,
    private val repository: Repository
) : ViewModel() {

    var isQueueFlush = MutableStateFlow(false)
    var queueBehaviorDescription = MutableStateFlow("")
    var voiceDescription = MutableStateFlow("")
    var speedDescription = MutableStateFlow("")
    var speed = MutableStateFlow(1f)

    fun initSettings(context: Context) {

        voiceDescription.value = "English - austrialian accent - male"
        speedDescription.value = speed.value.toString() // todo readfromdatastore
        isQueueFlush.value = readFromDataStore(context, isQueueFlushKey).toBoolean()
        setQueueSettingDescription(context)
    }

    // webhook
    val webhookUrl by lazy { webhookBaseUrl + Firebase.auth.currentUser?.uid }
    fun saveToClipboard(context: Context) {

        // save url to clipboard
        val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", webhookUrl)
        clipboardManager.setPrimaryClip(clip)
    }

    // voice
    fun getVoices(): List<Voice> {
        return tts.getVoices().toList()
    }

    // speed
    fun setSpeed(aSpeed: Float) {
        speed.value = aSpeed
        // todo writetodatastore
        speedDescription.value = speed.value.toString()
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
