package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.sommerengineering.baraudio.R
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

    // todo hoist up, generify onboarding screens

    val isInit = viewModel.tts.isInit.collectAsState()

    val text =
        if (isInit.value) "BarAudio uses text-to-speech to announce alerts."
        else "BarAudio requires text-to-speech, please install it to continue."

    val imageId =
        if (isInit.value) R.drawable.check_circle
        else R.drawable.cancel_circle

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
                    text = text,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge)
            }

            // image
            Image(
                modifier = Modifier
                    .size(2 * circularButtonSize)
                    .align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(imageId),
                contentDescription = null)

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
                        Row(
                            modifier = Modifier
                                .align(alignment = Alignment.Center)) {
                            Icon(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(12.dp),
                                tint = MaterialTheme.colorScheme.outline,
                                painter = painterResource(R.drawable.indicator_filled),
                                contentDescription = null)
                            Icon(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(12.dp),
                                tint = MaterialTheme.colorScheme.outline,
                                painter = painterResource(R.drawable.indicator_open),
                                contentDescription = null)
                            Icon(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(12.dp),
                                tint = MaterialTheme.colorScheme.outline,
                                painter = painterResource(R.drawable.indicator_open),
                                contentDescription = null)
                        }

                        // next button
                        Button(
                            modifier = Modifier
                                .align(alignment = Alignment.BottomEnd),
                            onClick = onNextClick) {
                            Text(
                                text = "Next")
                        }
                    }
            }
        }
    }
}