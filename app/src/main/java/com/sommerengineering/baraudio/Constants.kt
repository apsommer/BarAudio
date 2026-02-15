package com.sommerengineering.baraudio

import android.util.Log
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

// logs
const val TAG = "~~"
fun logMessage(msg: String?) = Log.v(TAG, "$msg")
fun logException(e: Exception) {
    Log.e(TAG, "handleException: ${e.message}", e)
    Firebase.crashlytics.recordException(e)
}

// durations
const val colorTransitionTimeMillis = 100
const val recentMessageTimeMillis = 3000
const val bottomBarTransitionTimeMillis = 1000

// urls
const val setupUrl = "https://sommerengineering.com/baraud.io"
const val privacyUrl = "https://sommerengineering.com/privacy_policy"
const val termsUrl = "https://sommerengineering.com/terms_and_conditions"

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val messagesNodeId = "messages"
const val whitelistNodeId = "whitelist"
const val usersNodeId = "users"
const val gitHubProviderId = "github.com"
const val messagesMaxSize = 1000

// notifications
const val channelId = "42"
const val channelName = "Webhook"
const val channelDescription = "Realtime data connection for BarAudio"
const val channelGroupId = "42"
const val channelGroupName = "Webhook"
const val isLaunchFromNotification = "isLaunchFromNotification"
const val broadcastKey = "broadcast"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val originKey = "origin"
const val defaultUtterance = "Thank you for using Bar Audio, please customize your webhook to continue!"
const val messageParsingError = "Error parsing message"

// topics
const val nqTopic = "NQ"

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
const val isShowQuoteKey = "showQuote"
const val isNQKey = "isNQ"
const val volumeKey = android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME

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
const val showQuoteTitle = "Mindfulness quote"
const val nqTitle = "Nasdaq-100 (NQ)"
const val esTitle = "S&P 500 (ES)"
const val btcTitle = "Bitcoin (BTC)" // todo "spot" not futures
const val systemTtsTitle = "System settings"
const val privacyTitle = "Privacy policy"
const val termsTitle = "Terms and conditions"
const val manageSubscriptionTitle = "Manage subscription"
const val signOutTitle = "Sign-out"

const val queueBehaviorFlushDescription = "Play new alerts immediately"
const val queueBehaviorAddDescription = "Add new alerts to queue"
const val uiDarkDescription = "Dark"
const val uiLightDescription = "Light"
const val screenFullDescription = "Full screen"
const val screenWindowedDescription = "Show system bars"
const val nqDescription = "Momentum & velocity"
const val esDescription = "Market participation"
const val btcDescription = "Volatility & extremes"
const val voiceDividerTitle = "VOICE"
const val uiDividerTitle = "THEME"
const val dataDividerTitle = "DATA"
const val legalDividerTitle = "LEGAL"

// images
const val deleteAllFadeDurationMillis = 1000
val loginButtonSize = 96.dp
val fabButtonSize = 72.dp
val edgePadding = 24.dp
val backgroundPadding = 48.dp
val settingsIconSize = edgePadding
val loginLogoPadding = 64.dp

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

// tts
const val defaultVoice = "en-gb-x-gbd-local"  // british, male

// origin
// https://www.tradingview.com/support/solutions/43000529348-about-webhooks/
// https://help.trendspider.com/kb/alerts/webhooks
val tradingview = listOf(
    "52.89.214.238",
    "34.212.75.30",
    "54.218.53.128",
    "52.32.178.7")
const val trendspider = "3.12.143.24"
const val insomnia = "84.123.224.196"
const val parsingErrorOrigin = "error"
const val localOrigin = "local"