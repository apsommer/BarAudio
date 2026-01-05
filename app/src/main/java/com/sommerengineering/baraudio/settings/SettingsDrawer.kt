package com.sommerengineering.baraudio.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.channelId
import com.sommerengineering.baraudio.dataDividerTitle
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.futuresWebhooksTitle
import com.sommerengineering.baraudio.screenTitle
import com.sommerengineering.baraudio.howToSetupTitle
import com.sommerengineering.baraudio.legalDividerTitle
import com.sommerengineering.baraudio.setupUrl
import com.sommerengineering.baraudio.manageSubscriptionTitle
import com.sommerengineering.baraudio.notificationsTitle
import com.sommerengineering.baraudio.pitchTitle
import com.sommerengineering.baraudio.privacyTitle
import com.sommerengineering.baraudio.privacyUrl
import com.sommerengineering.baraudio.queueBehaviorTitle
import com.sommerengineering.baraudio.signOutTitle
import com.sommerengineering.baraudio.speedTitle
import com.sommerengineering.baraudio.subscriptionUrl
import com.sommerengineering.baraudio.systemTtsPackageName
import com.sommerengineering.baraudio.systemTtsTitle
import com.sommerengineering.baraudio.termsTitle
import com.sommerengineering.baraudio.termsUrl
import com.sommerengineering.baraudio.uiDividerTitle
import com.sommerengineering.baraudio.uiModeTitle
import com.sommerengineering.baraudio.voiceDividerTitle
import com.sommerengineering.baraudio.voiceTitle
import com.sommerengineering.baraudio.webhookBaseUrl
import com.sommerengineering.baraudio.webhookTitle
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsDrawer(
    onSignOut: () -> Unit) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)
    val uriHandler = LocalUriHandler.current

    Scaffold { padding ->

        var isShowVoiceDialog by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier
                .padding(padding)) {

            // divider
            item {
                DividerItem(voiceDividerTitle)
            }

            // voice
            item {
                DialogSettingItem (
                    icon = R.drawable.voice,
                    title = voiceTitle,
                    description = viewModel.voiceDescription,
                    onClick = { isShowVoiceDialog = true }) {

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
                    title = speedTitle,
                    description = viewModel.speedDescription) {

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
                    title = pitchTitle,
                    description = viewModel.pitchDescription) {

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
                    title = queueBehaviorTitle,
                    description = viewModel.queueDescription) {

                        Switch(
                            checked = viewModel.isQueueAdd(),
                            onCheckedChange = { viewModel.setIsQueueAdd(context, it) })
                    }
            }

            // system tts settings
            item {
                LinkSettingItem(
                    icon = R.drawable.settings,
                    title = systemTtsTitle,
                    onClick = {
                        with(context) {
                            startActivity(
                                Intent(systemTtsPackageName)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    })
            }

            // divider
            item {
                DividerItem(uiDividerTitle)
            }

            // dark mode
            item {
                SwitchSettingItem(
                    icon = R.drawable.contrast,
                    title = uiModeTitle,
                    description = viewModel.uiModeDescription) {

                    Switch(
                        checked = viewModel.isDarkMode,
                        onCheckedChange = { viewModel.setIsDarkMode(context, it) })
                }
            }

            // full screen
            item {
                SwitchSettingItem(
                    icon = R.drawable.fullscreen,
                    title = screenTitle,
                    description = viewModel.fullScreenDescription) {

                        Switch(
                            checked = viewModel.isFullScreen,
                            onCheckedChange = { viewModel.setFullScreen(context, it) })
                    }
            }

            // divider
            item {
                DividerItem(dataDividerTitle)
            }

            // futures
            item {
                SwitchSettingItem(
                    icon = R.drawable.ear_listen,
                    title = futuresWebhooksTitle,
                    description = viewModel.futuresWebhooksDescription) {

                    Switch(
                        checked = viewModel.isFuturesWebhooks,
                        onCheckedChange = { viewModel.setFuturesWebhooks(context, it)})
                }
            }

            // webhook
            item {

                // set webhook url
                val webhookUrl = webhookBaseUrl + Firebase.auth.currentUser?.uid

                DialogSettingItem(
                    icon = R.drawable.webhook,
                    title = webhookTitle,
                    description = webhookUrl,
                    onClick = {
                        viewModel.saveToWebhookClipboard(
                            context = context,
                            webhookUrl = webhookUrl) }) {

                    IconButton(
                        onClick = {
                            viewModel.saveToWebhookClipboard(
                                context = context,
                                webhookUrl = webhookUrl) }) {
                        Icon(
                            painter = painterResource(R.drawable.copy),
                            contentDescription = null)
                    }
                }
            }

            // how to setup
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = howToSetupTitle,
                    onClick = { uriHandler.openUri(setupUrl) })
            }

            // divider
            item {
                DividerItem(legalDividerTitle)
            }

            // notification settings todo refactor to dialog
//            item {
//                LinkSettingItem(
//                    icon = R.drawable.notifications_active,
//                    title = notificationsTitle,
//                    onClick = {
//                        with(context) {
//                            startActivity(
//                                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
//                                    .putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
//                                    .putExtra(Settings.EXTRA_CHANNEL_ID, channelId))
//                        }
//                    })
//            }

            // privacy
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = privacyTitle,
                    onClick = { uriHandler.openUri(privacyUrl) })
            }

            // terms
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = termsTitle,
                    onClick = { uriHandler.openUri(termsUrl) })
            }

            // manage subscription
            item {
                LinkSettingItem(
                    icon = R.drawable.credit_card_gear,
                    title = manageSubscriptionTitle,
                    onClick = { uriHandler.openUri(subscriptionUrl) })
            }

            // sign-out
            item {
                LinkSettingItem(
                    icon = R.drawable.sign_out,
                    title = signOutTitle,
                    onClick = { onSignOut() })
            }

            // version code
            item {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = edgePadding, end = edgePadding, bottom = edgePadding),
                    text = BuildConfig.VERSION_NAME,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}