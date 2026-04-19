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
const val colorTransitionTimeMillis = 0
const val messageItemExpansionTimeMillis = 140

// urls
const val setupWebhookUrl = "https://sommerengineering.com/baraud.io"
const val termsAndConditionsUrl = "https://sommerengineering.com/terms_and_conditions"
const val privacyPolicyUrl = "https://sommerengineering.com/privacy_policy"

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
const val znStream = "ZN"
const val nqStream = "NQ"
const val esStream = "ES"
const val btcStream = "BTC"
const val gcStream = "GC"
const val siStream = "SI"

// user signals
const val userSignalDescription = "Custom signal"

// navigation
const val LoginRoute = "Login"
const val AppOnboardingRoute = "AppOnboarding"
const val AppOnboardingTextToSpeechRoute = "AppOnboardingTextToSpeech"
const val AppOnboardingNotificationsRoute = "AppOnboardingNotifications"
const val AppOnboardingWebhookRoute = "AppOnboardingWebhook"
const val MessagesRoute = "Messages"
const val SetupOnboardingRoute = "SetupOnboarding"
const val SetupOnboardingCopyWebhookRoute = "SetupOnboardingCopyWebhook"
const val SetupOnboardingPasteWebhookRoute = "SetupOnboardingPasteWebhook"
const val SetupOnboardingSignalArmedRoute = "SetupOnboardingSignalArmed"

// datastore
const val localCache = "localCache"
const val onboardingKey = "onboarding"
const val emptyStateKey = "emptyState"
const val voiceNameKey = "voice"
const val speedKey = "speed"
const val pitchKey = "pitch"
const val isMuteKey = "isMuteKey"
const val isFullScreenKey = "isFullScreen"
const val volumeKey = TextToSpeech.Engine.KEY_PARAM_VOLUME
const val feedModeKey = "feedMode"
const val isZNKey = "isZN"
const val isNQKey = "isNQ"
const val isESKey = "isES"
const val isBTCKey = "isBTC"
const val isGCKey = "isGC"
const val isSIKey = "isSI"

// billing
const val productId = "subscription" // match play store config
const val freeTrial = "free-trial" // match play store config
const val subscriptionUrl = "https://play.google.com/store/account/subscriptions?sku=" +
    productId + "&package=" + BuildConfig.APPLICATION_ID

// settings
const val voiceDividerTitle = "VOICE"
const val voiceTitle = "Voice"
const val speedTitle = "Speed"
const val pitchTitle = "Pitch"
const val systemTtsTitle = "System settings"
const val systemTtsDescription = "Install additional voices"
const val systemTtsInstallVoicesAction = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
const val streamsDividerTitle = "STREAMS"
const val premiumDividerTitle = "PREMIUM"
const val customTitle = "Custom signal"
const val customDividerTitle = "CUSTOM"
const val customDescription = "Webhook alerts"
const val displayDividerTitle = "DISPLAY"
const val screenTitle = "Screen"
const val screenFullDescription = "Full screen"
const val screenWindowedDescription = "Show system bars"
const val legalDividerTitle = "ACCOUNT"
const val manageSubscriptionTitle = "Manage subscription"
const val manageSubscriptionDescription = "Billing and plan"
const val signOutTitle = "Sign-out"
const val signOutDescription = "End session"

// images
val loginButtonSize = 96.dp
val fabSize = 72.dp
val edgePadding = 24.dp

// scrim logo and background
val logoAlpha = 0.25f
val backgroundAlpha = 0.5f

// item style
val assetIconSize = 28.dp
val settingsIconSize = 24.dp
val rowHorizontalPadding = 16.dp
val rowVerticalPadding = 12.dp
val rowIconPadding = 16.dp
val rowAccentWidth = 6.dp
val rowMinHeight = 72.dp
val dividerThickness = 0.5.dp

// onboarding
const val onboardingTotalPages = 3
const val appOnboardingTtsTitle = "Hear alerts instantly"
const val allowNotificationsMessage = "Stay updated in real time"
const val appOnboardingWebhookTitle = "Send alerts from any webhook"
const val setupOnboardingCopyTitle = "Copy your webhook URL"
const val setupOnboardingPasteTitle = "Paste into a TradingView alert\n"
const val setupOnboardingPasteSubtitle = "*Requires \"Essential\" plan or higher"
const val setupOnboardingSignalTitle = "Send a test alert"
const val nextText = "Next"
const val copyText = "Copy\nwebhook"
const val doneText = "Done"
const val soundAnimation = "sound.json"
const val notificationAnimation = "notification.json"
const val linkAnimation = "link.json"

// login
const val gitHubProviderId = "github.com"

// tts
const val defaultVoice = "en-gb-x-gbd-local"  // british, male
const val speedChangeUtterance = "Speed, "
const val pitchChangeUtterance = "Pitch, "
