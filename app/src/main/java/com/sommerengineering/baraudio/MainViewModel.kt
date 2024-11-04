package com.sommerengineering.baraudio

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    // webhook
    fun saveToClipboard(context: Context) {

        // todo save to clipboard

        Toast.makeText(
            context,
            webhookUrl,
            Toast.LENGTH_SHORT)
            .show()
    }

    // tts voice speed
    private val ttsSpeed: MutableStateFlow<Float> = MutableStateFlow(1f)
    fun setSpeed(speed: Float) { ttsSpeed.value = speed }

    // queue behavior
    val isQueueFlush = MutableStateFlow(false)
    val queueSettingDescription = MutableStateFlow(webhookUrl)

    fun setIsQueueFlush(
        context: Context,
        isChecked: Boolean) {

        isQueueFlush.value = isChecked

        val resId =
            if (isChecked) R.string.queue_behavior_flush_description
            else R.string.queue_behavior_add_description

        queueSettingDescription.value = context.getString(resId)
    }

    // todo auth state
    // todo alert list: local retrieval with room

}
