package com.sommerengineering.baraudio.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.onboarding.SetupScreen
import com.sommerengineering.baraudio.onboarding.SetupScreen.CopyWebhook
import com.sommerengineering.baraudio.onboarding.SetupScreen.PasteWebhook
import com.sommerengineering.baraudio.onboarding.SetupScreen.SignalArmed
import com.sommerengineering.baraudio.uitls.CopyWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.PasteWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.SignalArmedScreenRoute

@Composable
fun SetupNavigation(
    onClose: () -> Unit) {

    val controller = rememberNavController()

    NavHost(
        navController = controller,
        startDestination = CopyWebhookScreenRoute) {

        // copy webhook
        composable(CopyWebhookScreenRoute) {
            SetupScreen(
                setupStep = CopyWebhook,
                onClose = onClose
            )
        }

        // paste webhook
        composable(PasteWebhookScreenRoute) {
            SetupScreen(
                setupStep = PasteWebhook,
                onClose = onClose
            )
        }

        // signal armed (setup complete)
        composable(SignalArmedScreenRoute) {
            SetupScreen(
                setupStep = SignalArmed,
                onClose = onClose
            )
        }
    }
}