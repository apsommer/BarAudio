package com.sommerengineering.baraudio

import android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME

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
const val channelId = "42"
const val channelName = "Webhook"
const val channelDescription = "Push notifications for incoming webhook"
const val channelGroupId = "42"
const val channelGroupName = "Alerts"
const val isLaunchFromNotification = "isLaunchFromNotification"
const val uidKey = "uid"
const val timestampKey = "timestamp"
const val messageKey = "message"
const val originKey = "origin"
const val insomnia = "insomnia"
const val defaultMessage = "Thank you for using BarAudio!"
const val unauthenticatedTimestampNote = ", sign-in to hear message!"
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

// billing, configure in play store
const val productId = "premium"
const val freeTrial = "free-trial"
const val subscriptionUrl =
    "https://play.google.com/store/account/subscriptions?sku=" +
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