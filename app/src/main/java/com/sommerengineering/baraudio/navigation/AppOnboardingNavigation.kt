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
import com.sommerengineering.baraudio.onboarding.app.OnboardingHearAlerts
import com.sommerengineering.baraudio.onboarding.app.OnboardingSendAlerts
import com.sommerengineering.baraudio.onboarding.app.OnboardingStayUpdated
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.MessagesRoute
import com.sommerengineering.baraudio.uitls.OnboardingHearAlertsRoute
import com.sommerengineering.baraudio.uitls.OnboardingSendAlertsRoute
import com.sommerengineering.baraudio.uitls.OnboardingStayUpdatedRoute
import com.sommerengineering.baraudio.uitls.nextText
import com.sommerengineering.baraudio.uitls.onboardingHearAlertsSubTitle
import com.sommerengineering.baraudio.uitls.onboardingHearAlertsTitle
import com.sommerengineering.baraudio.uitls.onboardingSendAlertTitle
import com.sommerengineering.baraudio.uitls.onboardingSendAlertsSubtitle
import com.sommerengineering.baraudio.uitls.onboardingStayUpdatedSubtitle
import com.sommerengineering.baraudio.uitls.onboardingStayUpdatedTitle

fun NavGraphBuilder.AppOnboardingNavigation(
    controller: NavController,
    context: Context,
    viewModel: MainViewModel
) {

    navigation(
        route = AppOnboardingRoute,
        startDestination = OnboardingHearAlertsRoute
    ) {

        composable(OnboardingHearAlertsRoute) {
            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                title = onboardingHearAlertsTitle,
                subTitle = onboardingHearAlertsSubTitle,
                pageNumber = 0,
                buttonText = nextText,
                onNextClick = { controller.navigate(OnboardingStayUpdatedRoute) }) {
                OnboardingHearAlerts()
            }
        }

        composable(OnboardingStayUpdatedRoute) {

            // navigate forward after system notification request
            val hasRequested = viewModel.areNotificationsRequested
            LaunchedEffect(hasRequested) {
                if (!hasRequested) return@LaunchedEffect
                controller.navigate(OnboardingSendAlertsRoute)
            }

            BackHandler { (context as MainActivity).moveTaskToBack(true) }
            OnboardingScreen(
                title = onboardingStayUpdatedTitle,
                subTitle = onboardingStayUpdatedSubtitle,
                pageNumber = 1,
                buttonText = "Enable",
                onNextClick = {
                    if (Build.VERSION.SDK_INT >= 33) { // request notifications with system dialog
                        (context as MainActivity)
                            .requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        controller.navigate(OnboardingSendAlertsRoute)
                    } // old api notifications default to allowed
                }) {
                OnboardingStayUpdated()
            }
        }

        composable(OnboardingSendAlertsRoute) {
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
                OnboardingSendAlerts()
            }
        }
    }
}