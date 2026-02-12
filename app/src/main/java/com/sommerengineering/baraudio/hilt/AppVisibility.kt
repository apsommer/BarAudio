package com.sommerengineering.baraudio.hilt

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import kotlin.concurrent.Volatile

class AppVisibility @Inject constructor()
    : DefaultLifecycleObserver {

    @Volatile // atomic variable, visible to all threads (service and UI)
    var isForeground = false
        private set

    override fun onStart(owner: LifecycleOwner) { isForeground = true }
    override fun onStop(owner: LifecycleOwner) { isForeground = false }
}