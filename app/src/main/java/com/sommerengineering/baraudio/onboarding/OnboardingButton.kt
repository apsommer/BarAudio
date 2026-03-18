package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.next
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup
import com.sommerengineering.baraudio.uitls.onboardingTotalPages

@Composable
fun ColumnScope.OnboardingButton(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true) {

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = edgePadding)) {

            // page indicator
            Row(Modifier.align(Alignment.Center)) {

                for (i in 0 ..< onboardingTotalPages) {

                    val imageId =
                        if (i == pageNumber) R.drawable.indicator_filled
                        else R.drawable.indicator_open

                    Icon(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(12.dp),
                        tint = MaterialTheme.colorScheme.outline,
                        painter = painterResource(imageId),
                        contentDescription = null)
                }
            }

            // next button
            Button(
                modifier = Modifier.align(Alignment.BottomEnd),
                enabled = isNextEnabled,
                onClick = onNextClick) {

                val text = when (onboardingMode) {
                    AppOnboarding -> next
                    WebhookSetup -> when (pageNumber) {
                        0 -> next
                        1 -> "I pasted it"
                        2 -> "Done"
                        else -> ""
                }}

                Text(text)
            }
        }
    }
}

