package com.sommerengineering.baraudio.uitls

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.R

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
const val webhookBaseUrl =
    "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val streamsNode = "streams"
const val usersNode = "users"
const val tokensNode = "tokens"

// notifications
const val channelId = "42"
const val channelName = "Alerts"
const val channelDescription = "Real-time trading alerts"
const val channelGroupId = "42"
const val channelGroupName = "Signals"
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
const val OnboardingHearAlertsRoute = "AppOnboardingTextToSpeech"
const val OnboardingStayUpdatedRoute = "AppOnboardingNotifications"
const val OnboardingSendAlertsRoute = "AppOnboardingWebhook"
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
const val screenTitle = "Screen"
const val screenFullDescription = "Full screen"
const val screenWindowedDescription = "Show system bars"
const val generalDividerTitle = "GENERAL"
const val manageSubscriptionTitle = "Manage subscription"
const val manageSubscriptionDescription = "Billing and plan"
const val signOutTitle = "Sign-out"
const val signOutDescription = "End session"

// images
val loginButtonSize = 96.dp
val edgePadding = 24.dp

// style, general
val logoAlpha = 0.4f

@Composable
fun appBlue() = colorResource(R.color.app_blue)

@Composable
fun appGreen() = colorResource(R.color.app_green)

// item style
val rowHeight = 62.dp
val assetIconSize = 26.dp
val settingsIconSize = 24.dp
val rowHorizontalPadding = 16.dp
val rowVerticalPadding = 12.dp
val rowIconPadding = 16.dp
val rowAccentWidth = 6.dp
val dividerThickness = 0.5.dp
val descriptionAlpha = 0.5f
val streamDescriptionAlpha = 0.6f


// onboarding
const val onboardingTotalPages = 3
const val onboardingHearAlertsTitle = "Hear alerts instantly"
const val onboardingHearAlertsSubTitle = "We speak trading signals the moment\nthey happen."
const val onboardingStayUpdatedTitle = "Stay updated in real time"
const val onboardingStayUpdatedSubtitle =
    "Get every signal as it happens.\nNo delays. No filtering."
const val onboardingSendAlertTitle = "Send alerts from any webhook"
const val onboardingSendAlertsSubtitle = "Connect your tools. We'll speak\nyour signals out loud."
const val onboardingCopyWebhookTitle = "Copy your webhook URL"
const val onboardingCopyWebhookSubtitle = "Tap to copy."
const val onboardingPasteWebhookTitle = "Paste it into your alert"
const val onboardingPasteWebhookSubtitle = "Paste the URL into the webhook field."
const val onboardingListeningTitle = "Send a test alert"
const val onboardingListeningSubTitle = "We’ll confirm when it arrives."
const val nextText = "Next"
const val copyText = "Copy\nwebhook"
const val doneText = "Done"
const val enableText = "Enable"

// allow notifications banner
const val allowNotificationsMessage = "Allow notification to stay updates in real time"

// login
const val gitHubProviderId = "github.com"

// tts
const val defaultVoice = "en-gb-x-gbd-local"  // british, male
const val speedChangeUtterance = "Speed, "
const val pitchChangeUtterance = "Pitch, "
