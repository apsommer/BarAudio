package com.sommerengineering.baraudio

import android.content.Context
import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME
import android.text.format.DateUtils
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// logcat
const val TAG = "~"
fun logMessage(msg: String?) = Log.v(TAG, "$msg")
fun logException(e: Exception?) = Log.e(TAG, "handleException: ${e?.message}", e)

// todo extract to strings.xml

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val howToUseUrl = "https://baraud.io/" // todo
const val aboutUrl = "https://baraud.io/"
const val privacyUrl = "https://baraud.io/privacy/"
const val termsUrl = "https://baraud.io/terms/"
const val usersNode = "users"
const val messagesNode = "messages"
const val unauthenticatedUser = "unauthenticatedUser"
const val unauthenticatedToken = "unauthenticatedToken"
const val gitHubProviderId = "github.com"
const val messageMaxSize = 1000

// notifications
const val isLaunchFromNotification = "isLaunchFromNotification"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val originKey = "origin"
const val insomnia = "insomnia"
const val defaultMessage = "Thank you for using BarAudio!"
const val unauthenticatedTimestamp = ", please sign-in to hear message!"
const val parsingError = "Error parsing message"

// datastore
const val localCache = "localCache"
const val tokenKey = "token"
const val voiceKey = "voice"
const val speedKey = "speed"
const val pitchKey = "pitch"
const val isQueueFlushKey = "isQueueFlush"
const val isDarkModeKey = "isDarkMode"
const val volumeKey = KEY_PARAM_VOLUME

// settings
const val queueBehaviorFlushDescription = "Play new alerts immediately"
const val queueBehaviorAddDescription = "Add new alerts to queue"
const val uiModeDarkDescription = "Dark"
const val uiModeLightDescription = "Light"

// billing, configure in play store
const val productId = "premium"
const val freeTrial = "free-trial"
const val subscriptionUrl = "https://play.google.com/store/account/subscriptions?sku=" + productId + "&package=" + BuildConfig.APPLICATION_ID

fun beautifyTimestamp(timestamp: String): String {

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "MMMM dd, yyyy • h:mm:ss a" //  October 30, 2024 • 6:27:53 PM

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}

fun trimTimestamp(timestamp: String) =
    timestamp
        .substring(timestamp.length - 9, timestamp.length)
        .toInt()

fun readFromDataStore(
    context: Context,
    key: String) =
        runBlocking {
            context.dataStore.data
                .map { it[stringPreferencesKey(key)] }
                .first()
        }

fun writeToDataStore(
    context: Context,
    key: String,
    value: String) =
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
