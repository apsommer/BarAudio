package com.sommerengineering.signalvoice.onboarding.webhook

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.nextText
import com.sommerengineering.signalvoice.uitls.onboardingPasteWebhookSubtitle
import com.sommerengineering.signalvoice.uitls.onboardingPasteWebhookTitle

@Composable
fun PasteWebhookScreen(
    onNextClick: () -> Unit
) {

    OnboardingScreen(
        title = onboardingPasteWebhookTitle,
        subTitle = onboardingPasteWebhookSubtitle,
        pageNumber = 1,
        buttonText = nextText,
        onNextClick = onNextClick
    ) {

        Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ),
            painter = painterResource(R.drawable.screenshot),
            contentDescription = null
        )
    }
}