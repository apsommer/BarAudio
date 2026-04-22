package com.sommerengineering.baraudio.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.app.AppOnboardingTextToSpeech
import com.sommerengineering.baraudio.onboarding.app.OnboardingAllowNotifications
import com.sommerengineering.baraudio.uitls.AppOnboardingNotificationsRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingTextToSpeechRoute
import com.sommerengineering.baraudio.uitls.AppOnboardingWebhookRoute
import com.sommerengineering.baraudio.uitls.MessagesRoute
import com.sommerengineering.baraudio.uitls.nextText
import com.sommerengineering.baraudio.uitls.onboardingNotificationsSubtitle
import com.sommerengineering.baraudio.uitls.onboardingNotificationsTitle
import com.sommerengineering.baraudio.uitls.onboardingSendAlertTitle
import com.sommerengineering.baraudio.uitls.onboardingSendAlertsSubtitle
import com.sommerengineering.baraudio.uitls.onboardingTtsSubTitle
import com.sommerengineering.baraudio.uitls.onboardingTtsTitle

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
                title = onboardingTtsTitle,
                subTitle = onboardingTtsSubTitle,
                pageNumber = 0,
                buttonText = nextText,
                onNextClick = { controller.navigate(AppOnboardingNotificationsRoute) }) {
                AppOnboardingTextToSpeech()
            }
        }

        // onboarding screen: notifications
        composable(AppOnboardingNotificationsRoute) {

            // navigate forward after system notification request
            val hasRequested = viewModel.areNotificationsRequested
            LaunchedEffect(hasRequested) {
                if (!hasRequested) return@LaunchedEffect
                controller.navigate(AppOnboardingWebhookRoute)
            }

            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                title = onboardingNotificationsTitle,
                subTitle = onboardingNotificationsSubtitle,
                pageNumber = 1,
                buttonText = "Enable",
                onNextClick = {
                    if (Build.VERSION.SDK_INT >= 33) { // request notifications with system dialog
                        (context as MainActivity)
                            .requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else { controller.navigate(AppOnboardingWebhookRoute) } // old api notifications default to allowed
                }) {
                OnboardingAllowNotifications()
            }
        }

        // onboarding screen: webhook
        composable(AppOnboardingWebhookRoute) {
            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                title = onboardingSendAlertTitle,
                subTitle = onboardingSendAlertsSubtitle,
                pageNumber = 2,
                buttonText = nextText,
                onNextClick = {
                    viewModel.updateOnboarding(true)
                    controller.navigate(MessagesRoute) {
                        popUpTo(MessagesRoute) { inclusive = true }
                    }
                }) {
                OnboardingAllowNotifications()
            }
        }
    }
}