package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.loginButtonSize

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

        // sign-in text
        Spacer(Modifier.height(loginButtonSize / 2))
        Text(
            text = "Sign in to SignalVoice",
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(loginButtonSize / 2))

        // login buttons
        LoginButton(
            iconRes = R.drawable.google,
            iconRatio = 0.5f,
            onClick = { viewModel.signInWithGoogle(context, onAuthentication) })
        Spacer(Modifier.height(24.dp))
        LoginButton(
            iconRes = R.drawable.github,
            iconRatio = 0.55f,
            onClick = { viewModel.signInWithGitHub(context, onAuthentication) })
    }
}
