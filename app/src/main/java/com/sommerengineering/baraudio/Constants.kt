package com.sommerengineering.baraudio

import androidx.compose.ui.unit.dp

// durations
const val colorTransitionTimeMillis = 500
const val quoteFadeTimeMillis = 7000

// urls
const val setupUrl = "https://sommerengineering.com/baraud.io"
const val privacyUrl = "https://sommerengineering.com/privacy_policy"
const val termsUrl = "https://sommerengineering.com/terms_and_conditions"

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val usersNode = "users"
const val messagesNode = "messages"
const val whitelistNode = "whitelist"
const val unauthenticatedUser = "unauthenticatedUser"
const val unauthenticatedToken = "unauthenticatedToken"
const val gitHubProviderId = "github.com"
const val messageMaxSize = 1000

// notifications
const val channelId = "42"
const val channelName = "Webhook"
const val channelDescription = "Allow notifications to enable BarAudio"
const val channelGroupId = "42"
const val channelGroupName = "Webhook"
const val isLaunchFromNotification = "isLaunchFromNotification"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val originKey = "origin"
const val defaultMessage = "Thank you for using bar audio, please setup your webhook to continue!"
const val unauthenticatedTimestampNote = ", sign-in to hear message!"
const val parsingError = "Error parsing message"
const val buildTypeDebug = "debug"

// navigation
const val LoginScreenRoute = "LoginScreen"
const val OnboardingTextToSpeechScreenRoute = "OnboardingTextToSpeechScreen"
const val OnboardingNotificationsScreenRoute = "OnboardingNotificationsScreen"
const val OnboardingWebhookScreenRoute = "OnboardingWebhookScreen"
const val OnboardingCompleteRoute = "complete"
const val MessagesScreenRoute = "MessagesScreen"

// datastore
const val localCache = "localCache"
const val isFirstLaunchKey = "isFirstLaunch"
const val onboardingKey = "onboarding"
const val tokenKey = "token"
const val voiceKey = "voice"
const val speedKey = "speed"
const val pitchKey = "pitch"
const val isQueueFlushKey = "isQueueFlush"
const val isDarkModeKey = "isDarkMode"
const val isFullScreenKey = "isFullScreen"
const val showQuoteKey = "showQuote"
const val isFuturesWebhooksKey = "isFuturesWebhooks"
const val volumeKey = android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME

// billing
const val productId = "subscription" // match play store config
const val freeTrial = "free-trial" // match play store config
const val subscriptionUrl = "https://play.google.com/store/account/subscriptions?sku=" +
    productId + "&package=" + BuildConfig.APPLICATION_ID

// settings
const val systemTtsPackageName = "com.android.settings.TTS_SETTINGS"
const val howToSetupTitle = "How to customize"
const val webhookTitle = "Webhook"
const val voiceTitle = "Voice"
const val speedTitle = "Speed"
const val pitchTitle = "Pitch"
const val queueBehaviorTitle = "Queue"
const val uiModeTitle = "Theme"
const val screenTitle = "Screen"
const val showQuoteTitle = "Inspiration quote"
const val futuresWebhooksTitle = "Futures"
const val systemTtsTitle = "System settings"
const val privacyTitle = "Privacy policy"
const val termsTitle = "Terms and conditions"
const val manageSubscriptionTitle = "Manage subscription"
const val signOutTitle = "Sign-out"
const val queueBehaviorFlushDescription = "Play new alerts immediately"
const val queueBehaviorAddDescription = "Add new alerts to queue"
const val uiModeDarkDescription = "Dark"
const val uiModeLightDescription = "Light"
const val screenFullDescription = "Full screen"
const val screenWindowedDescription = "Show system"
const val futuresWebhookDescription = "NQ, ES, and GC at 1min"
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

// onboarding
const val onboardingTotalPages = 3
const val next = "Next"
const val ttsInstalledTitle = "BarAudio uses text-to-speech to announce alerts."
const val ttsNotInstalledTitle = "BarAudio requires text-to-speech, please install it to continue."
const val allowNotificationsTitle = "Allow notifications to recieve realtime events."
const val webhookStartTitle = "Customize your webhook with a simple "
const val webhookEndTitle = "setup"
const val period = "."
const val soundAnimation = "sound.json"
const val notificationAnimation = "notification.json"
const val linkAnimation = "link.json"
