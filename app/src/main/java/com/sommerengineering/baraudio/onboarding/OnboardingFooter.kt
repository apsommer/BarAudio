package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.onboardingTotalPages

@Composable
fun OnboardingFooter(
    buttonText: String,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true,
    modifier: Modifier) {

    Column(modifier) {

        Box(Modifier.fillMaxWidth()) {

            PageIndicator(
                pageNumber = pageNumber,
                modifier = Modifier.align(Alignment.Center))

            // next button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = edgePadding / 2)
                    .clip(MaterialTheme.shapes.small) // THIS fixes square ripple
                    .background(MaterialTheme.colorScheme.surfaceContainer) // subtle background
                    .clickable(onClick = onNextClick)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}