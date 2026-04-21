package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding

@Composable
fun OnboardingScreen(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true,
    onCloseClick: (() -> Unit)? = null,
    content: @Composable (() -> Unit)) {

    Box(Modifier
        .fillMaxSize()
        .padding(horizontal = edgePadding)) {

        Column(Modifier.fillMaxSize()) {

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

                content()

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

        // close button
        if (onCloseClick != null) {
            IconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = edgePadding)
                    .wrapContentWidth(Alignment.End),
                onClick = { onCloseClick() }) {

                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }
    }
}




