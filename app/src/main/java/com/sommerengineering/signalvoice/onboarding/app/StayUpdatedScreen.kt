package com.sommerengineering.signalvoice.onboarding.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.message.MessageItemUi
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.onboardingStayUpdatedSubtitle
import com.sommerengineering.signalvoice.uitls.onboardingStayUpdatedTitle

@Composable
fun StayUpdatedScreen(
    viewModel: MainViewModel,
    onNavigate: () -> Unit,
    onNextClick: () -> Unit,
    buttonText: String
) {

    val messages = onboardingMessages()

    // navigate forward after system notification request
    LaunchedEffect(Unit) {
        viewModel.notificationPermissionResult.collect {
            onNavigate()
        }
    }

    OnboardingScreen(
        title = onboardingStayUpdatedTitle,
        subTitle = onboardingStayUpdatedSubtitle,
        pageNumber = 1,
        buttonText = buttonText,
        onNextClick = onNextClick
    ) {

        BoxWithConstraints {

            // messages
            LazyColumn {
                items(messages) {
                    val state = getOnboardingMessageState(
                        message = it,
                        isExpanded = true
                    )
                    MessageItemUi(
                        state = state,
                        displayText = state.text, // todo temp
                        isShowDivider = true
                    )
                }
            }

            // scrim bottom area to imply feed
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(maxHeight * 0.2f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(0.9f)
                            )
                        )
                    )
            )
        }
    }
}