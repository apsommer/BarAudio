package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

@Composable
fun LoginScreen (
    onAuthentication: () -> Unit,
    modifier: Modifier = Modifier) {

    // initialize
    val context = LocalContext.current

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Row(
                Modifier.padding(top = 120.dp, bottom = 60.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painterResource(R.drawable.logo_full),
                    contentDescription = null,
                    Modifier.weight(3f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Column(
                Modifier.padding(60.dp)
            ) {
                Button(
                    onClick = {
                        signInWithGoogle(
                            activityContext = context,
                            onAuthentication = onAuthentication
                        )
                    },
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
}