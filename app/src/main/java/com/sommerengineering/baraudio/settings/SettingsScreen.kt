package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.aboutUrl
import com.sommerengineering.baraudio.privacyUrl
import com.sommerengineering.baraudio.termsUrl
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClicked: () -> Unit,
    onSignOut: () -> Unit) {

    // todo remove unused svg

    // init
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val viewModel: MainViewModel = koinViewModel(null, context as MainActivity)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null)
                }},
                title = {
                    Text(
                        text = stringResource(R.string.settings))
                })
        }) { scaffoldPadding ->

        var isShowVoiceDialog by remember { mutableStateOf(false) }

        LazyColumn(Modifier.padding(scaffoldPadding)) {

            // webhook
            item { SettingItem(
                icon = R.drawable.webhook,
                title = R.string.webhook_title,
                description = viewModel.webhookUrl,
                onClick = { viewModel.saveToClipboard(context) }) {
                IconButton( // todo refactor to Icon
                    modifier = Modifier.padding(vertical = 12.dp),
                    onClick = { viewModel.saveToClipboard(context) }) {
                    // todo play with .weight etc to show this icon correctly
                    Icon(
                        painter = painterResource(R.drawable.copy),
                        contentDescription = null)
            }}}

            // voice
            item { SettingItem(
                icon = R.drawable.voice,
                title = R.string.voice_title,
                description = viewModel.voiceDescription.value) {

                Icon(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .clickable { isShowVoiceDialog = true },
                    painter = painterResource(R.drawable.more_vertical),
                    contentDescription = null)

                if (isShowVoiceDialog) {
                    VoiceDialog(
                        viewModel = viewModel,
                        onItemSelected = {
                            viewModel.setVoice(context, it)
                            isShowVoiceDialog = false
                        },
                        onDismiss = { isShowVoiceDialog = false }
                    )
                }
            }}

            // speed
            item { SettingItem(
                icon = R.drawable.speed,
                title = R.string.speed_title,
                description = viewModel.speedDescription.value) {
                SliderImpl(
                    initPosition = viewModel.getSpeed(),
                    onValueChanged = { viewModel.setSpeed(context, it) })
            }}

            // pitch
            item { SettingItem(
                icon = R.drawable.pitch,
                title = R.string.pitch_title,
                description = viewModel.pitchDescription.value) {
                SliderImpl(
                    initPosition = viewModel.getPitch(),
                    onValueChanged = { viewModel.setPitch(context, it) })
            }}

            // queue behavior
            item { SettingItem(
                icon = R.drawable.text_to_speech,
                title = R.string.queue_behavior_title,
                description = viewModel.queueBehaviorDescription.value) {
                Switch(
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 12.dp),
                    checked = viewModel.isQueueAdd.collectAsState().value,
                    onCheckedChange = { viewModel.setIsQueueAdd(context, it) })
            }}

            // dark mode
            item { SettingItem(
                icon = R.drawable.contrast,
                title = R.string.ui_mode_title,
                description = viewModel.uiModeDescription.value) {
                Switch(
                    modifier = Modifier.padding(
                        horizontal = 24.dp,
                        vertical = 12.dp),
                    checked = viewModel.isDarkMode.value,
                    onCheckedChange = { viewModel.setIsDarkMode(context, it) })
            }}

            // about
            item { SettingItem(
                icon = R.drawable.browser,
                title = R.string.about_title,
                onClick = { uriHandler.openUri(aboutUrl) }) { }
            }

            // privacy
            item { SettingItem(
                icon = R.drawable.browser,
                title = R.string.privacy_title,
                onClick = { uriHandler.openUri(privacyUrl) }) { }
            }

            // terms
            item { SettingItem(
                icon = R.drawable.browser,
                title = R.string.terms_title,
                onClick = { uriHandler.openUri(termsUrl) }) { }
            }

            // sign-out
            item { SettingItem(
                icon = R.drawable.sign_out,
                title = R.string.sign_out_title,
                onClick = { onSignOut() }) { }
            }
        }
    }
}