package com.sommerengineering.baraudio.login

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.allowNotificationsTitle
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.linkAnimation
import com.sommerengineering.baraudio.next
import com.sommerengineering.baraudio.notificationAnimation
import com.sommerengineering.baraudio.onboardingTotalPages
import com.sommerengineering.baraudio.period
import com.sommerengineering.baraudio.setupUrl
import com.sommerengineering.baraudio.soundAnimation
import com.sommerengineering.baraudio.ttsInstalledTitle
import com.sommerengineering.baraudio.ttsNotInstalledTitle
import com.sommerengineering.baraudio.webhookEndTitle
import com.sommerengineering.baraudio.webhookStartTitle

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    pageNumber: Int,
    onNextClick: () -> Unit,
    isNextEnabled: Boolean = true) {

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
                        totalPages = onboardingTotalPages,
                        modifier = Modifier
                            .align(alignment = Alignment.Center))

                    // next button
                    Button(
                        modifier = Modifier
                            .align(alignment = Alignment.BottomEnd),
                        enabled = isNextEnabled,
                        onClick = onNextClick) {
                        Text(
                            text = next)
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

    val annotatedString =
        buildAnnotatedString { when (pageNumber) {

            0 -> {
                val text =
                    if (viewModel.tts.isInit.collectAsState().value) ttsInstalledTitle
                    else ttsNotInstalledTitle
                append(text)
            }

            1 -> {
                append(allowNotificationsTitle)
            }

            2 -> {
                append(webhookStartTitle)
                withLink(
                    link = LinkAnnotation.Url(
                        url = setupUrl,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary)))) {
                    append(webhookEndTitle)
                }
                append(period)
            }

            else -> append("")
        }
    }

    Text(
        text = annotatedString,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge)
}

@Composable
fun ColumnScope.OnboardingImage(
    pageNumber: Int) {

    val source = when (pageNumber) {
        0 -> soundAnimation
        1 -> notificationAnimation
        2 -> linkAnimation
        else -> ""
    }

    DotLottieAnimation(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        source = DotLottieSource.Asset(source),
        autoplay = true,
        loop = true,
        speed = 1f,
        useFrameInterpolation = false,
        playMode = Mode.FORWARD)
}

@Composable
fun PageIndicator(
    pageNumber: Int,
    totalPages: Int,
    modifier: Modifier) {

    // page indicator
    Row(
        modifier = modifier) {

        for (i in 0..< totalPages) {

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
