package com.sommerengineering.baraudio

import javax.inject.Inject
import kotlin.concurrent.Volatile

class ProcessState @Inject constructor() {

    // volatile means visible to all threads: service, UI, ...
    @Volatile
    var isTaskAlive = false
}