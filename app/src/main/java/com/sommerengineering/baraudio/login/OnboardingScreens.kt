package com.sommerengineering.baraudio.login

import android.content.Context
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import kotlinx.coroutines.flow.collect

@Composable
fun OnboardingTextToSpeechScreen(
    context: Context,
    viewModel: MainViewModel,
    onNextClick: () -> Unit) {

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()) {

            // text-to-speech installation status
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(64.dp), // todo extract
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(viewModel.getOnboardingTtsImageId()),
                    contentDescription = null)
            }
        }
    }
}