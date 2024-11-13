package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    // initialize common
    val context = LocalContext.current
    viewModel.initSettings(context)

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

            // webhook
            SettingItem(
                icon = R.drawable.webhook,
                title = R.string.webhook_title,
                description = viewModel.webhookUrl,
                onClick = { viewModel.saveToClipboard(context) }) {
                IconButton(
                    modifier = Modifier.padding(
//                        horizontal = 24.dp,
                        vertical = 12.dp),
                    onClick = { viewModel.saveToClipboard(context) }) {
                    // todo play with .weight etc to show this icon correctly
                    Icon(
                        painter = painterResource(R.drawable.copy),
                        contentDescription = null)
                }
            }

            // voice
            SettingItem(
                icon = R.drawable.voice,
                title = R.string.voice_title,
                description = viewModel.voiceDescription.collectAsState().value)  {
                VoiceDropdownMenu(viewModel.getVoices())
            }

            // speed
            SettingItem(
                icon = R.drawable.speed,
                title = R.string.speed_title,
                description = viewModel.speedDescription.collectAsState().value) {
                SpeedSlider(
                    initPosition = viewModel.getSpeed(),
                    onValueChanged = { viewModel.setSpeed(context, it) }
                )
            }

            // queue behavior
            SettingItem(
                icon = R.drawable.text_to_speech,
                title = R.string.queue_behavior_title,
                description = viewModel.queueBehaviorDescription.collectAsState().value) {
                Switch(
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 12.dp),
                    checked = viewModel.isQueueFlush.collectAsState().value,
                    onCheckedChange = { viewModel.setIsQueueFlush(context, it) })
            }

            // about
            SettingItem(
                icon = R.drawable.browser,
                title = R.string.about_title) { }

            // privacy
            SettingItem(
                icon = R.drawable.browser,
                title = R.string.privacy_title) { }

            // terms
            SettingItem(
                icon = R.drawable.browser,
                title = R.string.terms_title) { }

            // terms
            SettingItem(
                icon = R.drawable.sign_out,
                title = R.string.sign_out_title) { }
        }
    }
}