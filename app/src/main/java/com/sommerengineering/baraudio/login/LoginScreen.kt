package com.sommerengineering.baraudio.login

import android.content.Context
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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage

@Composable
fun LoginScreen (
    onAuthentication: () -> Unit,
    modifier: Modifier = Modifier) {

    // init
    val context = LocalContext.current

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
                    painter = painterResource(R.drawable.google),
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
                    painter = painterResource(R.drawable.github),
                    contentDescription = null)
            }
        }
    }
}

fun signInWithGitHub (
    context: Context,
    onAuthentication: () -> Unit) {

    // init
    val firebaseAuth = FirebaseAuth.getInstance()
    val provider = OAuthProvider.newBuilder("github.com")

    // todo optional account suggestion with hint
    // provider.addCustomParameters(mapOf("login" to "your-email@gmail.com"))

    // todo additional scopes must be preconfigured in app's api permissions, don't need it?
    // provider.scopes = listOf("user:email")

    // Signing in via this method puts your Activity in the background, which means that it can be
    // reclaimed by the system during the sign in flow. In order to make sure that you don't make
    // the user try again if this happens, you should check if a result is already present.
    val pendingTaskResult = firebaseAuth.pendingAuthResult
    if (pendingTaskResult != null) {

        // scenario above has occurred, finish sign-in flow
        pendingTaskResult
            .addOnSuccessListener { handleSuccess(it) }
            .addOnFailureListener { logException(it) }

    // start sign-in flow
    } else {

        firebaseAuth
            .startActivityForSignInWithProvider(
                context as MainActivity,
                provider.build())
            .addOnSuccessListener { handleSuccess(it) }
            .addOnFailureListener { logException(it) }
    }
}

fun handleSuccess(authResult: AuthResult) {

    logMessage("Github sign-in success!")

    val idpData = authResult.additionalUserInfo?.profile
    logMessage(idpData.toString())

    val oathToken = (authResult.credential as OAuthCredential).accessToken
    logMessage(oathToken)

    val oathSecret = (authResult.credential as OAuthCredential).secret
    logMessage(oathSecret)
}
