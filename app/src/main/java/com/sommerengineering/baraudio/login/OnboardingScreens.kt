package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainApplication
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.appModule
import com.sommerengineering.baraudio.circularButtonSize
import com.sommerengineering.baraudio.edgePadding
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import org.koin.dsl.koinApplication

@Composable
fun OnboardingTextToSpeechScreen(
    viewModel: MainViewModel,
    onNextClick: () -> Unit) {

    Surface {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(edgePadding),
            verticalArrangement = Arrangement.Center) {

            // text
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = viewModel.getOnboardingTtsText(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge)
            }

            // image
            Image(
                modifier = Modifier
                    .size(2 * circularButtonSize)
                    .align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(viewModel.getOnboardingTtsImageId()),
                contentDescription = null)

            // next button
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End) {

                Button(
                    onClick = onNextClick) {
                    Text(
                        text = "Next")
                }
            }
        }
    }
}