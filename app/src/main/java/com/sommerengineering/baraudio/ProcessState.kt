package com.sommerengineering.baraudio

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.Volatile

@Singleton
class ProcessState @Inject constructor() {

    // volatile means visible to all threads: service, UI, ...
    @Volatile
    var isTaskAlive = false
}