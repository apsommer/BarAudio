package com.sommerengineering.baraudio.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.app.HearAlertsScreen
import com.sommerengineering.baraudio.onboarding.app.SendAlertsScreen
import com.sommerengineering.baraudio.onboarding.app.StayUpdatedScreen
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.MessagesRoute
import com.sommerengineering.baraudio.uitls.OnboardingHearAlertsRoute
import com.sommerengineering.baraudio.uitls.OnboardingSendAlertsRoute
import com.sommerengineering.baraudio.uitls.OnboardingStayUpdatedRoute

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

            val onNextClick = { controller.navigate(OnboardingStayUpdatedRoute) }

            HearAlertsScreen(
                onNextClick = onNextClick
            )
        }

        composable(OnboardingStayUpdatedRoute) {

            val hasRequested = viewModel.areNotificationsRequested
            val onNavigate = { controller.navigate(OnboardingSendAlertsRoute) }
            val onNextClick = {
                if (Build.VERSION.SDK_INT >= 33) { // request notifications with system dialog
                    (context as MainActivity)
                        .requestNotificationPermissionLauncher
                        .launch(Manifest.permission.POST_NOTIFICATIONS)
                } else { // notifications default to allowed for older api
                    onNavigate()
                }
            }

            StayUpdatedScreen(
                hasRequested = hasRequested,
                onNavigate = onNavigate,
                onNextClick = onNextClick
            )
        }

        composable(OnboardingSendAlertsRoute) {

            val onNextClick = {
                viewModel.updateOnboarding(true)
                controller.navigate(MessagesRoute) {
                    popUpTo(MessagesRoute) { inclusive = true }
                }
            }

            SendAlertsScreen(
                onNextClick = onNextClick
            )
        }
    }
}