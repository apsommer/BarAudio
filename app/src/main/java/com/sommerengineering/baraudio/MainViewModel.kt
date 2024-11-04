package com.sommerengineering.baraudio

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    // webhook

    fun saveToClipboard() {
        logMessage("Copied https://...")
        // todo save to clipboard
        // todo toast message
    }

    // tts voice speed
    private val ttsSpeed: MutableStateFlow<Float> = MutableStateFlow(1f)
    fun setSpeed(speed: Float) { ttsSpeed.value = speed }

    // queue behavior
    val isQueueFlush = MutableStateFlow(false)
    val queueSettingDescription = MutableStateFlow(R.string.queue_behavior_add_description)
    fun setIsQueueFlush(isChecked: Boolean) {
        isQueueFlush.value = isChecked
        if (isChecked) queueSettingDescription.value = R.string.queue_behavior_flush_description
        else queueSettingDescription.value = R.string.queue_behavior_add_description
    }

    // todo auth state
    // todo alert list: local retrieval with room

}