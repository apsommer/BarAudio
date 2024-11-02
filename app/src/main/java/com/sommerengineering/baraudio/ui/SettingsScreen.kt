package com.sommerengineering.baraudio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel = koinViewModel(),
    onBackClicked: () -> Unit, ) {

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings))
                })
        }) { scaffoldPadding ->

        Column(Modifier.padding(scaffoldPadding)) {

            // todo
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Webhook, display in description, copy icon and function right side")
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Voice picker, exposed dropdown")
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Voice speed, slider")

            SettingSwitchItem(
                icon = R.drawable.text_to_speech,
                title = R.string.queue_behavior_title,
                state = viewModel.isQueueFlush.collectAsState(),
                onClick = { viewModel.setIsQueueFlush(it) })

            // todo
            Text(
                modifier = Modifier.padding(24.dp),
                text = "About, external link to website")
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Privacy Policy, external link to website")
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Terms and Conditions, external link to website")
            Text(
                modifier = Modifier.padding(24.dp),
                text = "Sign out, firebase sign-out and return to login screen")
        }
    }
}