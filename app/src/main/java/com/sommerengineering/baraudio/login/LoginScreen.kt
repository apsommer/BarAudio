package com.sommerengineering.baraudio.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.loginButtonSize

@Composable
fun LoginScreen (
    viewModel: MainViewModel,
    onAuthentication: () -> Unit) {

    val context = LocalContext.current
    val isDarkMode = viewModel.isDarkMode
    val gitHubImageId =
        if (isDarkMode) R.drawable.github_light
        else R.drawable.github_dark

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // logo
        Image(
            modifier = Modifier.fillMaxWidth(0.7f),
            painter = painterResource(R.drawable.logo_full),
            contentDescription = null)

        Spacer(Modifier.height(48.dp))

        // google
        Column(
            verticalArrangement = Arrangement.Center) {
            Box(Modifier
                .size(loginButtonSize)
                .clip(CircleShape)
                .clickable { viewModel.signInWithGoogle(context, onAuthentication) }
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                    shape = CircleShape)) {
                Image(
                    modifier = Modifier.size(loginButtonSize),
                    painter = painterResource(R.drawable.google),
                    contentDescription = null)
            }

            Spacer(Modifier.height(24.dp))

            // github
            Box(Modifier
                .size(loginButtonSize)
                .clip(CircleShape)
                .clickable { viewModel.signInWithGitHub(context, onAuthentication) }
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
                    shape = CircleShape)) {
                Box(Modifier
                    .align(Alignment.Center)
                    .size(loginButtonSize + 2.dp) // touch over half circularButtonSize to align with google
                    .clip(CircleShape)) {
                    Image(
                        painter = painterResource(gitHubImageId),
                        contentDescription = null)
                }
            }
        }
    }

}
