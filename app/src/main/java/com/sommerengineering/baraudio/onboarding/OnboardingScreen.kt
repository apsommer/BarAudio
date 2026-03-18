package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
    isNextEnabled: Boolean = true) {

    Surface {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(edgePadding),
            verticalArrangement = Arrangement.Center) {

            // title
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center) {
                OnboardingText(
                    onboardingMode = onboardingMode,
                    pageNumber = pageNumber)
            }

            // animation, or image
            OnboardingImage(
                onboardingMode = onboardingMode,
                pageNumber = pageNumber)

            // next button and page indicator
            OnboardingButton(
                onboardingMode = onboardingMode,
                pageNumber = pageNumber,
                onNextClick = onNextClick,
                isNextEnabled = isNextEnabled)
        }
    }
}





