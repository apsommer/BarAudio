package com.sommerengineering.baraudio.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val account: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

@Composable
fun LoginScreen (
    modifier: Modifier = Modifier) {

    // todo get state

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { },
                modifier = Modifier.size(
                    width = 120.dp,
                    height = 60.dp
                )
            ) {
                Text(
                    text = "Sign in with Google ..."
                )
            }
        }
    }

}