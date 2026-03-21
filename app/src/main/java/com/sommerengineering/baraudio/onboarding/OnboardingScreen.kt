package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.uitls.edgePadding

@Composable
fun OnboardingScreen(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true,
    content: @Composable (() -> Unit)? = null) {

    Box(Modifier
        .fillMaxSize()
        .padding(horizontal = edgePadding)) {

        // title
        OnboardingText(
            onboardingMode = onboardingMode,
            pageNumber = pageNumber,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = edgePadding * 5))

        // dynamic content: animation, image, verification ui, ...
        if (content != null) {
            content()
        } else {
            OnboardingImage(
                onboardingMode = onboardingMode,
                pageNumber = pageNumber,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }

        // page indicators and button
        OnboardingButton(
            onboardingMode = onboardingMode,
            pageNumber = pageNumber,
            onNextClick = onNextClick,
            isNextEnabled = isNextEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .padding(bottom = edgePadding * 2))
    }
}





