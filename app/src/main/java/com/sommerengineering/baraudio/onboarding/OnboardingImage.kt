package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.linkAnimation
import com.sommerengineering.baraudio.uitls.notificationAnimation
import com.sommerengineering.baraudio.uitls.soundAnimation
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup

@Composable
fun ColumnScope.OnboardingImage(
    onboardingMode: OnboardingMode,
    pageNumber: Int) {

    when (onboardingMode) {

        AppOnboarding -> {

            val animationPath = when (pageNumber) {
                0 -> soundAnimation
                1 -> notificationAnimation
                2 -> linkAnimation
                else -> ""
            }

            DotLottieAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                source = DotLottieSource.Asset(animationPath),
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = false,
                playMode = Mode.FORWARD)
        }

        WebhookSetup -> {

            val resId = when (pageNumber) {
                0 -> R.drawable.webhook
                1 -> R.drawable.ungroup
                2 -> R.drawable.group
                else -> R.drawable.webhook
            }

            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                painter = painterResource(resId),
                contentDescription = null)
        }
    }
}