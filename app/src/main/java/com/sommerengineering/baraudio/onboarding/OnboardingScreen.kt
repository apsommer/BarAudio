package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = edgePadding)
    ) {

        // title
        OnboardingText(
            onboardingMode = onboardingMode,
            pageNumber = pageNumber,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = edgePadding * 5))

        // dynamic content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = edgePadding * 2),
            contentAlignment = Alignment.Center) {
            content?.let { it() } ?: // image, verification ui, ...
            OnboardingAnimation( // app onboarding animation
                pageNumber = pageNumber,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center))
        }

        // page indicators and button
        OnboardingButton(
            onboardingMode = onboardingMode,
            pageNumber = pageNumber,
            onNextClick = onNextClick,
            isNextEnabled = isNextEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = edgePadding * 2))
    }
}





