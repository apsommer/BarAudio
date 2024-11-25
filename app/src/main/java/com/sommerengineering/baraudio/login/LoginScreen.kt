package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen (
    onAuthentication: () -> Unit,
    modifier: Modifier = Modifier) {

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(80.dp)) {

            // logo
            Image(
                modifier = Modifier
                    .padding(
                        top = 80.dp),
                painter = painterResource(R.drawable.logo_full),
                contentDescription = null)

            Spacer(modifier = Modifier
                .height(160.dp))

            // google
            LoginButton(
                imageId = viewModel.getGoogleImageId(),
                imagePadding = 4.dp, // rawSize 40.dp = content 20.dp + padding top/bottom 10.dp
                text = "Sign in with Google",
                onClick = {
                    signInWithGoogle(
                        context = context,
                        onAuthentication = onAuthentication)
                })

            Spacer(modifier = Modifier
                .height(40.dp))

            // github
            LoginButton(
                imageId = viewModel.getGitHubImageId(),
                imagePadding = 18.dp,
                text = "Sign in with GitHub",
                onClick = {
                    signInWithGitHub(
                        context = context,
                        onAuthentication = onAuthentication)
                })
        }
    }
}
