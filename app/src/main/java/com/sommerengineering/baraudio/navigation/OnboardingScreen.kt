package com.sommerengineering.baraudio.navigation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.linkAnimation
import com.sommerengineering.baraudio.uitls.next
import com.sommerengineering.baraudio.uitls.notificationAnimation
import com.sommerengineering.baraudio.uitls.onboardingTotalPages
import com.sommerengineering.baraudio.uitls.soundAnimation

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onboardingMode: OnboardingMode,
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center) {
                OnboardingText(
                    onboardingMode = onboardingMode,
                    pageNumber = pageNumber)
            }

            // image
            OnboardingImage(
                onboardingMode = onboardingMode,
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
                        Text(text = next)
                    }
                }
            }
        }
    }
}





