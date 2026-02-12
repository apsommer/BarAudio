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
import com.sommerengineering.baraudio.loginButtonSize
import com.sommerengineering.baraudio.loginLogoPadding

@Composable
fun LoginScreen (
    viewModel: MainViewModel,
    onAuthentication: () -> Unit) {

    val context = LocalContext.current
    val isDarkMode = viewModel.isDarkMode
    val gitHubImageId =
        if (isDarkMode) R.drawable.github_light
        else R.drawable.github_dark

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()) {

            // logo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(loginLogoPadding),
                verticalArrangement = Arrangement.Center) {
                Image(
                    painter = painterResource(R.drawable.logo_full),
                    contentDescription = null)
            }

            // google
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center) {

                Box(
                    modifier = Modifier
                        .size(loginButtonSize)
                        .clip(CircleShape)
                        .clickable {
                            signInWithGoogle(
                                context = context,
                                credentialManager = viewModel.credentialManager,
                                onAuthentication = onAuthentication)
                        }
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface),
                            shape = CircleShape)) {

                    Image(
                        modifier = Modifier
                            .size(loginButtonSize),
                        painter = painterResource(R.drawable.google),
                        contentDescription = null)
                }

                Spacer(
                    modifier = Modifier
                        .height(loginButtonSize / 2))

                // github
                Box(
                    modifier = Modifier
                        .size(loginButtonSize)
                        .clip(CircleShape)
                        .clickable {
                            signInWithGitHub(
                                context = context,
                                onAuthentication = onAuthentication)
                        }
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface),
                            shape = CircleShape)) {

                    Box(
                        modifier = Modifier
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
}
