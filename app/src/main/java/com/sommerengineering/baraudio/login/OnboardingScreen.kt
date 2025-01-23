package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.circularButtonSize
import com.sommerengineering.baraudio.edgePadding

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    pageNumber: Int,
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
                OnboardingText(
                    viewModel = viewModel,
                    pageNumber = pageNumber)
            }

            // image
            OnboardingImage(
                viewModel = viewModel,
                pageNumber = pageNumber)

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
                    PageIndicator(
                        pageNumber = pageNumber,
                        totalPages = 3, // todo extract or hoist
                        modifier = Modifier
                            .align(alignment = Alignment.Center))

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

@Composable
fun OnboardingText(
    viewModel: MainViewModel,
    pageNumber: Int) {

    // todo extract

    val text = when (pageNumber) {

        0 -> {
            if (viewModel.tts.isInit.collectAsState().value) "BarAudio uses text-to-speech to announce alerts."
            else "BarAudio requires text-to-speech, please install it to continue."
        }

        1 -> {
            "BarAudio uses push notifications for realtime triggers. Please select \"Allow\" in the following request."
        }

        2 -> {
            "BarAudio uses webhooks for market connection. Setup is simple, just copy and paste your unique ID as shown **here**."
        }

        else -> ""
    }

    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge)
}

@Composable
fun ColumnScope.OnboardingImage(
    viewModel: MainViewModel,
    pageNumber: Int) {

    when (pageNumber) {

        0 -> {

            val isTtsInit by viewModel.tts.isInit.collectAsState()

            val imageId =
                if (isTtsInit) R.drawable.check_circle
                else R.drawable.cancel_circle

//            Image(
//                modifier = Modifier
//                    .size(2 * circularButtonSize)
//                    .align(alignment = Alignment.CenterHorizontally),
//                painter = painterResource(imageId),
//                contentDescription = null)

            DotLottieAnimation(
                modifier = Modifier
                    .size(2 * circularButtonSize)
                    .align(alignment = Alignment.CenterHorizontally),
                source = DotLottieSource.Asset("check.json"),
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = false,
                playMode = Mode.FORWARD)
        }

        // show system permission request dialog as "image"
        1 -> {
//            Image(
//                modifier = Modifier
//                    .size(2 * circularButtonSize)
//                    .align(alignment = Alignment.CenterHorizontally),
//                painter = painterResource(R.drawable.notifications_circle),
//                colorFilter = ColorFilter.tint(
//                    color = colorResource(R.color.logo_blue)
//                ),
//                contentDescription = null)
            DotLottieAnimation(
                modifier = Modifier
                    .size(2 * circularButtonSize)
                    .align(alignment = Alignment.CenterHorizontally),
                source = DotLottieSource.Asset("notification.json"),
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = false,
                playMode = Mode.FORWARD)
        }

        2 -> {
//            Image(
//                modifier = Modifier
//                    .size(2 * circularButtonSize)
//                    .align(alignment = Alignment.CenterHorizontally),
//                painter = painterResource(R.drawable.webhook),
//                colorFilter = ColorFilter.tint(
//                    color = colorResource(R.color.logo_blue)
//                ),
//                contentDescription = null)
            DotLottieAnimation(
                modifier = Modifier
                    .size(2 * circularButtonSize)
                    .align(alignment = Alignment.CenterHorizontally),
                source = DotLottieSource.Asset("link.json"),
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = false,
                playMode = Mode.FORWARD)
        }

        else -> { }
    }
}

@Composable
fun PageIndicator(
    pageNumber: Int,
    totalPages: Int,
    modifier: Modifier) {

    // page indicator
    Row(
        modifier = modifier) {

        for (i in 0..totalPages-1) {

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
}

