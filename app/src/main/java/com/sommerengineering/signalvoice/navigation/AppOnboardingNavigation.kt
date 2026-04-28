package com.sommerengineering.signalvoice.navigation

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.signalvoice.MainActivity
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.onboarding.app.HearAlertsScreen
import com.sommerengineering.signalvoice.onboarding.app.SendAlertsScreen
import com.sommerengineering.signalvoice.onboarding.app.StayUpdatedScreen
import com.sommerengineering.signalvoice.uitls.AppOnboardingRoute
import com.sommerengineering.signalvoice.uitls.MessagesRoute
import com.sommerengineering.signalvoice.uitls.OnboardingHearAlertsRoute
import com.sommerengineering.signalvoice.uitls.OnboardingSendAlertsRoute
import com.sommerengineering.signalvoice.uitls.OnboardingStayUpdatedRoute
import com.sommerengineering.signalvoice.uitls.enableText
import com.sommerengineering.signalvoice.uitls.nextText

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

            val hasRequested = viewModel.hasRequestedNotificationPermission
            val onNavigate = { controller.navigate(OnboardingSendAlertsRoute) }
            val onNextClick = {
                if (hasRequested || Build.VERSION.SDK_INT < 33) {
                    onNavigate()
                } else {
                    (context as MainActivity)
                        .requestNotificationPermissionLauncher
                        .launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            val buttonText = if (hasRequested) nextText else enableText

            StayUpdatedScreen(
                viewModel = viewModel,
                onNavigate = onNavigate,
                onNextClick = onNextClick,
                buttonText = buttonText
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