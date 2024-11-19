package com.sommerengineering.baraudio

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

const val localCache = "localCache"

const val tokenBaseKey = "_token"
const val voiceBaseKey = "_voice"
const val speedBaseKey = "_speed"
const val pitchBaseKey = "_pitch"
const val isQueueFlushBaseKey = "_isQueueFlush"
const val isDarkModeBaseKey = "_isDarkMode"

private val uid = Firebase.auth.currentUser?.uid
val tokenKey by lazy { uid + tokenBaseKey }
val voiceKey by lazy { uid + voiceBaseKey }
val speedKey by lazy { uid + speedBaseKey }
val pitchKey by lazy { uid + pitchBaseKey }
val isQueueAddKey by lazy { uid + isQueueFlushBaseKey }
val isDarkModeKey by lazy { uid + isDarkModeBaseKey }

// todo extract to strings.xml
const val queueBehaviorFlushDescription = "Play new alerts immediately"
const val queueBehaviorAddDescription = "Add new alerts to queue"
const val uiModeDarkDescription = "Dark"
const val uiModeLightDescription = "Light"

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