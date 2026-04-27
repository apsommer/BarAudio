package com.sommerengineering.signalvoice.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.sommerengineering.signalvoice.uitls.onboardingTotalPages

@Composable
fun PageIndicator(
    pageNumber: Int,
    modifier: Modifier
) {

    val displayText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append("${pageNumber + 1}")
        }

        append(" OF ${onboardingTotalPages}")
    }

    Text(
        modifier = modifier,
        text = displayText,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}