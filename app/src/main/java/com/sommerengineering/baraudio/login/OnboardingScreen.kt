package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.circularButtonSize
import com.sommerengineering.baraudio.edgePadding

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    pageNumber: Int,
    onNextClick: () -> Unit) {

    val text = viewModel.getOnboardingText(pageNumber)
    val imageId = viewModel.getOnboardingImageId(pageNumber)

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