package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val account: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

@Composable
fun LoginScreen (
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier) {

    // todo get state

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(60.dp)
        ) {
            Image(
                painterResource(id = R.drawable.logo_full),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(60.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sign in with Google ..."
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sign in with Github ..."
                )
            }
        }
    }

}