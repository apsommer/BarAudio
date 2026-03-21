package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingMode.SetupWebhook
import com.sommerengineering.baraudio.uitls.linkAnimation
import com.sommerengineering.baraudio.uitls.notificationAnimation
import com.sommerengineering.baraudio.uitls.soundAnimation

@Composable
fun OnboardingImage(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    modifier: Modifier) {

    when (onboardingMode) {

        AppOnboarding -> {

            val animationPath = when (pageNumber) {
                0 -> soundAnimation
                1 -> notificationAnimation
                2 -> linkAnimation
                else -> ""
            }

            DotLottieAnimation(
                modifier = modifier,
                source = DotLottieSource.Asset(animationPath),
                autoplay = true,
                loop = true,
                speed = 1f,
                useFrameInterpolation = false,
                playMode = Mode.FORWARD)
        }

        SetupWebhook -> {

            val resId = when (pageNumber) {
                0 -> R.drawable.copy
                1 -> R.drawable.screenshot
                else -> R.drawable.error // not possible
            }

            Image(
                modifier = modifier,
                painter = painterResource(resId),
                contentDescription = null)
        }
    }
}