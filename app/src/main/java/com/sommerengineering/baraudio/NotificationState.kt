package com.sommerengineering.baraudio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class NotificationState @Inject constructor() {
    private val _enabled = MutableStateFlow(false)
    val enabled = _enabled.asStateFlow()
    fun update(value: Boolean) { _enabled.value = value }
}