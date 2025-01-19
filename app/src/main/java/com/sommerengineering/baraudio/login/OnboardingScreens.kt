package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel

@Composable
fun OnboardingTextToSpeechScreen(
    viewModel: MainViewModel,
    onNextClick: () -> Unit) {

    Surface {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)) {

            Text(
                text = viewModel.getOnboardingTtsText(),
                style = MaterialTheme.typography.titleLarge)

            // text-to-speech installation status
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center) {
                Image(
                    modifier = Modifier.size(160.dp),
                    painter = painterResource(viewModel.getOnboardingTtsImageId()),
                    contentDescription = null)
            }

            // next button
            Button(
                onClick = onNextClick,
                modifier = Modifier

                    .align(
                        alignment = Alignment.End
                    )) {

                    Text(
                        text = "Next")
            }
        }
    }
}