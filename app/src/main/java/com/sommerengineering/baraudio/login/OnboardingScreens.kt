package com.sommerengineering.baraudio.login

import android.content.Context
import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.circularButtonSize
import kotlinx.coroutines.flow.collect

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
                    modifier = Modifier.size(128.dp),
                    painter = painterResource(viewModel.getOnboardingTtsImageId()),
                    contentDescription = null)
            }

            // next button
            Button(
                onClick = onNextClick,
                modifier = Modifier) {

                    Text(
                        text = "Next")
            }
        }
    }
}