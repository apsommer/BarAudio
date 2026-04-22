package com.sommerengineering.baraudio.onboarding

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
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
            Button(
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = isNextEnabled,
                onClick = onNextClick) {
                Text(
                    textAlign = TextAlign.Center,
                    text = buttonText)
            }
        }
    }
}