package com.sommerengineering.baraudio.uitls

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.sommerengineering.baraudio.BuildConfig

// logs
const val TAG = "~~"
fun logMessage(msg: String?) = Log.v(TAG, "$msg")
fun logException(e: Exception) {
    Log.e(TAG, "handleException: ${e.message}", e)
    Firebase.crashlytics.recordException(e)
}

// durations
const val bottomBarTransitionTimeMillis = 1000
const val colorTransitionTimeMillis = 100
const val messageItemExpansionTimeMillis = 140

// urls
const val setupUrl = "https://sommerengineering.com/baraud.io"
// const val privacyUrl = "https://sommerengineering.com/privacy_policy"
// const val termsUrl = "https://sommerengineering.com/terms_and_conditions"

// room
const val roomDatabaseName = "messages.db"

// firebase database
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val streamsNode = "streams"
const val usersNode = "users"
const val tokensNode = "tokens"

// notifications
const val channelId = "42"
const val channelName = "Webhook"
const val channelDescription = "Realtime data connection for BarAudio"
const val channelGroupId = "42"
const val channelGroupName = "Webhook"
const val notificationId = 42
const val notificationKey = "notification"
const val streamKey = "stream"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val sourceKey = "source"

// streams
const val nqStream = "NQ"
const val esStream = "ES"
const val btcStream = "BTC"
const val gcStream = "GC"

// user signals
const val userSignalDescription = "Custom webhook"

// navigation
const val LoginScreenRoute = "LoginScreen"
const val OnboardingTextToSpeechScreenRoute = "OnboardingTextToSpeechScreen"
const val OnboardingNotificationsScreenRoute = "OnboardingNotificationsScreen"
const val OnboardingWebhookScreenRoute = "OnboardingWebhookScreen"
const val MessagesScreenRoute = "MessagesScreen"

// datastore
const val localCache = "localCache"
const val onboardingKey = "onboarding"
const val voiceNameKey = "voice"
const val speedKey = "speed"
const val pitchKey = "pitch"
const val isQueueAddKey = "isQueueAdd"
const val isMuteKey = "isMuteKey"
const val isDarkModeKey = "isDarkMode"
const val isFullScreenKey = "isFullScreen"
const val volumeKey = TextToSpeech.Engine.KEY_PARAM_VOLUME
const val feedModeKey = "feedMode"
const val isNQKey = "isNQ"
const val isGCKey = "isGC"

// billing
const val productId = "subscription" // match play store config
const val freeTrial = "free-trial" // match play store config
const val subscriptionUrl = "https://play.google.com/store/account/subscriptions?sku=" +
    productId + "&package=" + BuildConfig.APPLICATION_ID

// settings
const val systemTtsPackageName = "com.android.settings.TTS_SETTINGS"
const val howToSetupTitle = "Customize your webhook"
const val webhookTitle = "Webhook"
const val voiceTitle = "Voice"
const val speedTitle = "Speed"
const val pitchTitle = "Pitch"
const val queueBehaviorTitle = "Queue"
const val uiModeTitle = "Theme"
const val screenTitle = "Screen"
const val systemTtsTitle = "System settings"
const val manageSubscriptionTitle = "Manage subscription"
const val signOutTitle = "Sign-out"
const val voiceDividerTitle = "VOICE"
const val uiDividerTitle = "THEME"
const val dataDividerTitle = "STREAM"
const val legalDividerTitle = "ACCOUNT"
const val queueFlushDescription = "Play new alerts immediately"
const val queueAddDescription = "Add new alerts to queue"
const val uiDarkDescription = "Dark"
const val uiLightDescription = "Light"
const val screenFullDescription = "Full screen"
const val screenWindowedDescription = "Show system bars"

// images
val loginButtonSize = 96.dp
val fabSize = 72.dp
val fabPadding = 16.dp
val edgePadding = 24.dp
val settingsIconSize = 24.dp
val loginLogoPadding = 64.dp
val assetIconSize = 28.dp

// onboarding
const val onboardingTotalPages = 3
const val next = "Next"
const val ttsInstalledTitle = "BarAudio uses text-to-speech to announce alerts."
const val ttsNotInstalledTitle = "BarAudio requires text-to-speech, please install it to continue."
const val allowNotificationsTitle = "Allow notifications for realtime data."
const val webhookStartTitle = "Customize your webhook with a simple "
const val webhookEndTitle = "setup"
const val period = "."
const val soundAnimation = "sound.json"
const val notificationAnimation = "notification.json"
const val linkAnimation = "link.json"

// login
const val gitHubProviderId = "github.com"

// tts
const val defaultVoice = "en-gb-x-gbd-local"  // british, male
const val speedChangeUtterance = "Speed, "
const val pitchChangeUtterance = "Pitch, "
