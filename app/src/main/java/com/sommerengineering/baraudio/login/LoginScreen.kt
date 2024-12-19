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
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.buttonBorderSize
import com.sommerengineering.baraudio.isUpdateRequired
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen (
    onAuthentication: () -> Unit,
    onForceUpdate: () -> Unit,
    modifier: Modifier = Modifier) {

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    // size composables
    val githubImageSize = 50.dp // touch over half buttonBorderSize to align with google
    val logoPadding = 64.dp

    Surface {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()) {

            // logo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(logoPadding),
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
                        .size(buttonBorderSize)
                        .clip(CircleShape)
                        .clickable {
                            signInWithGoogle(
                                context = context,
                                onAuthentication = onAuthentication,
                                onForceUpdate = onForceUpdate)
                        }
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface),
                            shape = CircleShape)) {

                    Image(
                        modifier = Modifier
                            .size(buttonBorderSize),
                        painter = painterResource(viewModel.getGoogleImageId()),
                        contentDescription = null)
                }

                Spacer(
                    modifier = Modifier
                        .height(buttonBorderSize / 2))

                // github
                Box(
                    modifier = Modifier
                        .size(buttonBorderSize)
                        .clip(CircleShape)
                        .clickable {
                            signInWithGitHub(
                                context = context,
                                onAuthentication = onAuthentication,
                                onForceUpdate = onForceUpdate)
                        }
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface),
                            shape = CircleShape)) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(githubImageSize)
                            .clip(CircleShape)) {

                        Image(
                            painter = painterResource(viewModel.getGitHubImageId()),
                            contentDescription = null)
                    }
                }
            }
        }
    }
}
