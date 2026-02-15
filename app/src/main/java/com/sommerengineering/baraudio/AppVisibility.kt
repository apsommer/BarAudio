package com.sommerengineering.baraudio

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import kotlin.concurrent.Volatile

class AppVisibility @Inject constructor()
    : DefaultLifecycleObserver {

    // volatile means visible to all threads: service, UI, ...
    @Volatile
    var isForeground = false
        private set

    override fun onStart(owner: LifecycleOwner) { isForeground = true }
    override fun onStop(owner: LifecycleOwner) { isForeground = false }
}