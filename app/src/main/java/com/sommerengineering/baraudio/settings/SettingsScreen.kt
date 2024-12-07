package com.sommerengineering.baraudio.settings

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.aboutUrl
import com.sommerengineering.baraudio.howToUseUrl
import com.sommerengineering.baraudio.privacyUrl
import com.sommerengineering.baraudio.termsUrl
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onSignOut: () -> Unit) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)
    val uriHandler = LocalUriHandler.current

    Scaffold { padding ->

        var isShowVoiceDialog by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier
                .padding(padding)) {

            // how to use
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = R.string.how_to_use,
                    onClick = { uriHandler.openUri(howToUseUrl) })
            }

            // webhook todo make entire item clickable
            item {
                DialogSettingItem(
                    icon = R.drawable.webhook,
                    title = R.string.webhook,
                    description = viewModel.webhookUrl,
                    onClick = { viewModel.saveToWebhookClipboard(context) }) {

                    IconButton(
                        onClick = { viewModel.saveToWebhookClipboard(context) }) {
                        Icon(
                            painter = painterResource(R.drawable.copy),
                            contentDescription = null)
                    }

                    // todo toast for older api
                }
            }

            // voice
            item {
                DialogSettingItem (
                    icon = R.drawable.voice,
                    title = R.string.voice,
                    description = viewModel.voiceDescription.value) {

                    IconButton(
                        onClick = { isShowVoiceDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.more_vertical),
                            contentDescription = null)
                    }

                        if (isShowVoiceDialog) {
                            VoiceDialog(
                                viewModel = viewModel,
                                onItemSelected = {
                                    viewModel.setVoice(context, it)
                                    isShowVoiceDialog = false
                                },
                                onDismiss = {
                                    isShowVoiceDialog = false
                                })
                        }
                }
            }

            // speed
            item {
                SliderSettingItem(
                    icon = R.drawable.speed,
                    title = R.string.speed,
                    description = viewModel.speedDescription.value) {

                        SliderImpl(
                            initPosition = viewModel.getSpeed(),
                            onValueChanged = { viewModel.setSpeed(context, it) },
                            onValueChangeFinished = { viewModel.speakLastMessage() })
                    }
            }

            // pitch
            item {
                SliderSettingItem(
                    icon = R.drawable.pitch,
                    title = R.string.pitch,
                    description = viewModel.pitchDescription.value) {

                        SliderImpl(
                            initPosition = viewModel.getPitch(),
                            onValueChanged = { viewModel.setPitch(context, it) },
                            onValueChangeFinished = { viewModel.speakLastMessage() })
                        }
            }

            // queue behavior
            item {
                SwitchSettingItem(
                    icon = R.drawable.text_to_speech,
                    title = R.string.queue_behavior,
                    description = viewModel.queueBehaviorDescription.value) {

                        Switch(
                            checked = viewModel.isQueueAdd(),
                            onCheckedChange = { viewModel.setIsQueueAdd(context, it) })
                    }
            }

            // dark mode
            item {
                SwitchSettingItem(
                    icon = R.drawable.contrast,
                    title = R.string.ui_mode,
                    description = viewModel.uiModeDescription.value) {

                        Switch(
                            checked = viewModel.isDarkMode.value,
                            onCheckedChange = { viewModel.setIsDarkMode(context, it) })
                    }
            }

            // system tts settings
            item {
                LinkSettingItem(
                    icon = R.drawable.settings,
                    title = R.string.system_tts,
                    onClick = {
                        with(context) {
                            startActivity(
                                Intent(getString(R.string.system_tts_settings_package_name))
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    })
            }

            // about
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = R.string.about,
                    onClick = { uriHandler.openUri(aboutUrl) })
            }

            // privacy
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = R.string.privacy,
                    onClick = { uriHandler.openUri(privacyUrl) })
            }

            // terms
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = R.string.terms,
                    onClick = { uriHandler.openUri(termsUrl) })
            }

            // todo manage subscription
            //  https://developer.android.com/google/play/billing/subscriptions#link-specific

            // sign-out
            item {
                LinkSettingItem(
                    icon = R.drawable.sign_out,
                    title = R.string.sign_out,
                    onClick = { onSignOut() })
            }
        }
    }
}