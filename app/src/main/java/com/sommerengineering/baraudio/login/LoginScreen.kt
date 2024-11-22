package com.sommerengineering.baraudio.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
            modifier = modifier.fillMaxSize()) {
            Row(
                Modifier.padding(top = 120.dp, bottom = 60.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painterResource(R.drawable.logo_full),
                    contentDescription = null,
                    Modifier.weight(3f))
                Spacer(modifier = Modifier.weight(1f))
            }
            Column(
                Modifier.padding(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clickable {
                            signInWithGoogle(
                                context = context,
                                onAuthentication = onAuthentication)
                    },
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(viewModel.getGoogleImageId()),
                    contentDescription = null)
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    modifier = Modifier
                        .padding(24.dp)
                        .clickable {
                            signInWithGitHub(
                                context = context,
                                onAuthentication = onAuthentication)
                        },
                    alignment = Alignment.Center,
                    painter = painterResource(viewModel.getGitHubImageId()),
                    contentDescription = null)
            }
        }
    }
}
