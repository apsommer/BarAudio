package com.sommerengineering.baraudio.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Image(
                modifier = Modifier
                    .padding(
                        top = 80.dp),
                painter = painterResource(R.drawable.logo_full),
                contentDescription = null)
            Spacer(
                modifier = Modifier
                    .height(160.dp))
            Text(
                text = "Sign in with ...",
                style = MaterialTheme.typography.bodyLarge)
            Spacer(
                modifier = Modifier
                    .height(40.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable {
                        signInWithGoogle(
                            context = context,
                            onAuthentication = onAuthentication)
                    }
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface),
                        shape = CircleShape)) {
                Image(
                    modifier = Modifier
                        .size(80.dp),
                    painter = painterResource(viewModel.getGoogleImageId()),
                    contentDescription = null)
            }
            Spacer(
                modifier = Modifier
                    .height(40.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
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
                        .size(42.dp)
                        .clip(CircleShape)) {
                    Image(
                        painter = painterResource(viewModel.getGitHubImageId()),
                        contentDescription = null)
                }
            }
        }
    }
}
