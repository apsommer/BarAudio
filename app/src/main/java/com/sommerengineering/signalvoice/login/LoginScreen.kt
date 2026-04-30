package com.sommerengineering.signalvoice.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.R

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onAuthentication: () -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-16).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // logo with scrim
        Box(
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Image(
                painter = painterResource(R.drawable.appbar_compact),
                contentDescription = null
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(0.7f))
            )
        }

        // login buttons
        Spacer(Modifier.height(48.dp))
        LoginButton(
            iconRes = R.drawable.google,
            iconRatio = 0.5f,
            onClick = { viewModel.signInWithGoogle(context, onAuthentication) })
        Spacer(Modifier.height(24.dp))
        LoginButton(
            iconRes = R.drawable.github,
            iconRatio = 0.55f,
            onClick = { viewModel.signInWithGitHub(context, onAuthentication) })

        // guest login
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Continue as guest",
            color = MaterialTheme.colorScheme.primary.copy(0.9f),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .offset(y = 4.dp)
                .background(MaterialTheme.colorScheme.background) // keeps tap clean over scrim if needed
                .clickable { onAuthentication() }
        )
    }
}
