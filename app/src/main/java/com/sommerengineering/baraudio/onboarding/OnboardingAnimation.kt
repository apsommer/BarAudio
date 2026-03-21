package com.sommerengineering.baraudio.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.uitls.linkAnimation
import com.sommerengineering.baraudio.uitls.notificationAnimation
import com.sommerengineering.baraudio.uitls.soundAnimation

@Composable
fun OnboardingAnimation(
    pageNumber: Int,
    modifier: Modifier) {

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