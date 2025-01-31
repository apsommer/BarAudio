package com.sommerengineering.baraudio

import androidx.compose.ui.unit.dp

// baraudio urls
const val setupUrl = "https://baraud.io/setup"
const val aboutUrl = "https://baraud.io/"
const val privacyUrl = "https://baraud.io/privacy/"
const val termsUrl = "https://baraud.io/terms/"

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://us-central1-com-sommerengineering-baraudio.cloudfunctions.net/baraudio?uid="
const val usersNode = "users"
const val messagesNode = "messages"
const val unauthenticatedUser = "unauthenticatedUser"
const val unauthenticatedToken = "unauthenticatedToken"
const val gitHubProviderId = "github.com"
const val messageMaxSize = 1000

// notifications
const val channelId = "42"
const val channelName = "Webhook"
const val channelDescription = "Notifications for incoming webhook"
const val channelGroupId = "42"
const val channelGroupName = "Alerts"
const val isLaunchFromNotification = "isLaunchFromNotification"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val originKey = "origin"
const val defaultMessage = "Thank you for using BarAudio, please setup your webhook to continue!"
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
const val volumeKey = android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME

// billing
const val productId = "subscription" // config in play store
const val freeTrial = "free-trial" // config in play store
const val subscriptionUrl = "https://play.google.com/store/account/subscriptions?sku=" +
    productId + "&package=" + BuildConfig.APPLICATION_ID

// settings
const val systemTtsPackageName = "com.android.settings.TTS_SETTINGS"
const val howToUseTitle = "How to use"
const val webhookTitle = "Webhook"
const val voiceTitle = "Voice"
const val speedTitle = "Speed"
const val pitchTitle = "Pitch"
const val queueBehaviorTitle = "Queue behavior"
const val uiModeTitle = "Theme"
const val systemTtsTitle = "TTS system settings"
const val notificationsTitle = "Notifications"
const val aboutTitle = "About"
const val privacyTitle = "Privacy policy"
const val termsTitle = "Terms and conditions"
const val manageSubscriptionTitle = "Manage subscription"
const val signOutTitle = "Sign-out"
const val queueBehaviorFlushDescription = "Play new alerts immediately"
const val queueBehaviorAddDescription = "Add new alerts to queue"
const val uiModeDarkDescription = "Dark"
const val uiModeLightDescription = "Light"

// images
const val deleteAllFadeDurationMillis = 1000
val circularButtonSize = 96.dp // login, fab, ...
val edgePadding = 24.dp
val settingsIconSize = edgePadding

// onboarding
const val onboardingTotalPages = 3
const val next = "Next"
const val ttsInstalledTitle = "BarAudio uses text-to-speech to announce alerts."
const val ttsNotInstalledTitle = "BarAudio requires text-to-speech, please install it to continue."
const val allowNotificationsTitle = "Please allow notifications for realtime events."
const val webhookStartTitle = "BarAudio connects to your unique webhook with a simple "
const val webhookEndTitle = "setup"
const val period = "."
const val soundAnimation = "sound.json"
const val notificationAnimation = "notification.json"
const val linkAnimation = "link.json"
