package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.uitls.edgePadding

@Composable
fun OnboardingScreen(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit)? = null) {

    Surface {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(edgePadding),
            verticalArrangement = Arrangement.Center) {

            OnboardingText(
                onboardingMode = onboardingMode,
                pageNumber = pageNumber)

            // dynamic content for webhook setup, step 2 (paste)
            if (content != null) {
                content()
            } else {
                OnboardingImage(
                    onboardingMode = onboardingMode,
                    pageNumber = pageNumber)
            }

            OnboardingButton(
                onboardingMode = onboardingMode,
                pageNumber = pageNumber,
                onNextClick = onNextClick,
                isNextEnabled = isNextEnabled)
        }
    }
}





