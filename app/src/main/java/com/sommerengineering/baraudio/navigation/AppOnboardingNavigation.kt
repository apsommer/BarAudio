package com.sommerengineering.baraudio.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.app.AppOnboardingTextToSpeech
import com.sommerengineering.baraudio.onboarding.app.NodeConnection
import com.sommerengineering.baraudio.onboarding.app.PulseRings
import com.sommerengineering.baraudio.onboarding.app.VoicePulseGraphic
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingNotificationsRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingTextToSpeechRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingWebhookRoute
import com.sommerengineering.baraudio.uitls.MessagesRoute

fun NavGraphBuilder.AppOnboardingNavigation(
    controller: NavController,
    context: Context,
    viewModel: MainViewModel) {

    navigation(
        route = AppOnboardingRoute,
        startDestination = AppOnboardingTextToSpeechRoute) {

        // onboarding screen: text-to-speech
        composable(AppOnboardingTextToSpeechRoute) {
            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                onboardingMode = AppOnboarding,
                pageNumber = 0,
                onNextClick = { controller.navigate(AppOnboardingNotificationsRoute) }) {
                AppOnboardingTextToSpeech()
            }
        }

        // onboarding screen: notifications
        composable(AppOnboardingNotificationsRoute) {

            // ask for permission again if the first request is declined
            val areNotificationsEnabled = viewModel.areNotificationsEnabled
            val count = remember { mutableIntStateOf(0) }

            // navigate forward if notifications are granted
            LaunchedEffect(areNotificationsEnabled) {
                if (areNotificationsEnabled && Build.VERSION.SDK_INT >= 33) {
                    controller.navigate(AppOnboardingWebhookRoute)
                }
            }

            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                onboardingMode = AppOnboarding,
                pageNumber = 1,
                onNextClick = {
                    if (Build.VERSION.SDK_INT >= 33 && 2 > count.intValue) {
                        (context as MainActivity).requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                        count.intValue++
                    } else {
                        controller.navigate(AppOnboardingWebhookRoute)
                    }
                }
            ) {
                PulseRings()
            }
        }

        // onboarding screen: webhook
        composable(AppOnboardingWebhookRoute) {
            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                onboardingMode = AppOnboarding,
                pageNumber = 2,
                onNextClick = {
                    viewModel.updateOnboarding(true)
                    controller.navigate(MessagesRoute) {
                        popUpTo(MessagesRoute) { inclusive = true }
                    }
                }) {
                NodeConnection()
            }
        }
    }
}