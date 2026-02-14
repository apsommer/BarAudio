package com.sommerengineering.baraudio.settings

import android.content.Intent
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
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.dataDividerTitle
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.nqTopicDescription
import com.sommerengineering.baraudio.nqTopicTitle
import com.sommerengineering.baraudio.howToSetupTitle
import com.sommerengineering.baraudio.legalDividerTitle
import com.sommerengineering.baraudio.manageSubscriptionTitle
import com.sommerengineering.baraudio.pitchTitle
import com.sommerengineering.baraudio.privacyTitle
import com.sommerengineering.baraudio.privacyUrl
import com.sommerengineering.baraudio.queueBehaviorTitle
import com.sommerengineering.baraudio.screenTitle
import com.sommerengineering.baraudio.setupUrl
import com.sommerengineering.baraudio.showQuoteTitle
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

@Composable
fun SettingsDrawer(
    viewModel: MainViewModel,
    onSignOut: () -> Unit) {

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val speed = viewModel.speed
    val pitch = viewModel.pitch
    val isQueueAdd = viewModel.isQueueAdd

    val voiceDescription = viewModel.voiceDescription
    val speedDescription = viewModel.speedDescription
    val pitchDescription = viewModel.pitchDescription
    val queueDescription = viewModel.queueDescription

    val isShowQuote = viewModel.isShowQuote
    val isFuturesWebhooks = viewModel.isFuturesWebhooks
    val isFullScreen = viewModel.isFullScreen
    val fullScreenDescription = viewModel.fullScreenDescription

    val isDarkMode = viewModel.isDarkMode
    val uiModeDescription = viewModel.darkModeDescription

    var isShowVoiceDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(start = 0.dp, end = 0.dp, top = edgePadding / 2, bottom = 0.dp)) {

            // divider
            item {
                DividerItem(voiceDividerTitle)
            }

            // voice
            item {
                DialogSettingItem (
                    icon = R.drawable.voice,
                    title = voiceTitle,
                    description = voiceDescription,
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
                                viewModel.setVoice(it)
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
                    description = speedDescription) {

                        SliderImpl(
                            initPosition = speed,
                            onValueChanged = { viewModel.updateSpeed(it) },
                            onValueChangeFinished = { viewModel.speakLastMessage() })
                    }
            }

            // pitch
            item {
                SliderSettingItem(
                    icon = R.drawable.pitch,
                    title = pitchTitle,
                    description = pitchDescription) {

                        SliderImpl(
                            initPosition = pitch,
                            onValueChanged = { viewModel.updatePitch(it) },
                            onValueChangeFinished = { viewModel.speakLastMessage() })
                        }
            }

            // queue behavior
            item {
                SwitchSettingItem(
                    icon = R.drawable.text_to_speech,
                    title = queueBehaviorTitle,
                    description = queueDescription) {

                        Switch(
                            checked = isQueueAdd,
                            onCheckedChange = { viewModel.updateQueueAdd(it) })
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
                DividerItem(dataDividerTitle)
            }

            // futures
            item {
                SwitchSettingItem(
                    icon = R.drawable.ear_listen,
                    title = nqTopicTitle,
                    description = nqTopicDescription) {

                    Switch(
                        checked = isFuturesWebhooks,
                        onCheckedChange = { viewModel.updateFuturesWebhooks(it)})
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
                    onClick = { viewModel.saveToWebhookClipboard(webhookUrl) }) {

                    IconButton(
                        onClick = { viewModel.saveToWebhookClipboard(webhookUrl) }) {
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
                DividerItem(uiDividerTitle)
            }

            // full screen
            item {
                SwitchSettingItem(
                    icon = R.drawable.fullscreen,
                    title = screenTitle,
                    description = fullScreenDescription) {

                    Switch(
                        checked = isFullScreen,
                        onCheckedChange = { viewModel.updateFullScreen(it) })
                }
            }

            // theme
            item {
                SwitchSettingItem(
                    icon = R.drawable.contrast,
                    title = uiModeTitle,
                    description = uiModeDescription) {

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.updateDarkMode(it) })
                }
            }

            // quote
            item {
                SwitchSettingItem(
                    icon = R.drawable.cognition,
                    title = showQuoteTitle) {

                    Switch(
                        checked = isShowQuote,
                        onCheckedChange = { viewModel.updateShowQuote(it)})
                }
            }

            // divider
            item {
                DividerItem(legalDividerTitle)
            }

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