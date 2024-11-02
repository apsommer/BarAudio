package com.sommerengineering.baraudio

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val repository: Repository
) : ViewModel() {

    // todo auth state
    // todo alert list: local retrieval with room

    // tts voice speed
    private val _ttsSpeed: MutableStateFlow<Float> = MutableStateFlow(1f)
    var ttsSpeed = _ttsSpeed.asStateFlow()
    fun setSpeed(speed: Float) { _ttsSpeed.value = speed }
}