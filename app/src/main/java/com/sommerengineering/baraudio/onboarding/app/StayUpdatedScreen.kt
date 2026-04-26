package com.sommerengineering.baraudio.onboarding.app

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
import com.sommerengineering.baraudio.message.LinearMessageItem
import com.sommerengineering.baraudio.onboarding.BackgroundGlowContainer
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.enableText
import com.sommerengineering.baraudio.uitls.onboardingStayUpdatedSubtitle
import com.sommerengineering.baraudio.uitls.onboardingStayUpdatedTitle

@Composable
fun StayUpdatedScreen(
    hasRequested: Boolean,
    onNavigate: () -> Unit,
    onNextClick: () -> Unit
) {

    val messages = onboardingMessages()

    // navigate forward after system notification request
    LaunchedEffect(hasRequested) {
        if (!hasRequested) return@LaunchedEffect
        onNavigate()
    }

    OnboardingScreen(
        title = onboardingStayUpdatedTitle,
        subTitle = onboardingStayUpdatedSubtitle,
        pageNumber = 1,
        buttonText = enableText,
        onNextClick = onNextClick
    ) {

        BackgroundGlowContainer {

            BoxWithConstraints {

                // messages
                LazyColumn {
                    items(messages) {
                        val state = getOnboardingMessageState(
                            message = it,
                            isExpanded = true
                        )
                        LinearMessageItem(
                            state = state,
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
}