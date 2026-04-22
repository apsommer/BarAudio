package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingText(
    title: String,
    subTitle: String? = null,
    modifier: Modifier) {

    Column(modifier) {

        // title
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        // subtitle
        if (subTitle != null) {
            Text(
                text = subTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge)
        }
    }
}