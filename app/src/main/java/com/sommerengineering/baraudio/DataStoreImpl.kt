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
val tokenKey by lazy { Firebase.auth.currentUser?.uid ?: unauthenticatedUser }
const val voiceKey = "voice"
const val speedKey = "speed"
const val pitchKey = "pitch"
const val isQueueAddKey = "isQueueFlush"
const val isDarkModeKey = "isDarkMode"

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
}