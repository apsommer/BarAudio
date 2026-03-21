package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding
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
        modifier = Modifier.fillMaxSize().offset(y = (-16).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Image(
            modifier = Modifier.fillMaxWidth(0.6f),
            painter = painterResource(R.drawable.logo_full),
            contentDescription = null)
        Spacer(Modifier.height(loginButtonSize))
        LoginButton(
            iconRes = R.drawable.google,
            iconRatio = 0.5f,
            onClick = { viewModel.signInWithGoogle(context, onAuthentication) })
        Spacer(Modifier.height(24.dp))
        LoginButton(
            iconRes = gitHubImageId,
            iconRatio = 0.55f,
            onClick = { viewModel.signInWithGitHub(context, onAuthentication) })
    }
}
