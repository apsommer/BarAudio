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
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.aboutTitle
import com.sommerengineering.baraudio.aboutUrl
import com.sommerengineering.baraudio.channelId
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.howToUseTitle
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
import com.sommerengineering.baraudio.uiModeTitle
import com.sommerengineering.baraudio.voiceTitle
import com.sommerengineering.baraudio.webhookBaseUrl
import com.sommerengineering.baraudio.webhookTitle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
                    title = howToUseTitle,
                    onClick = { uriHandler.openUri(setupUrl) })
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

            // notification settings
            item {
                LinkSettingItem(
                    icon = R.drawable.notifications_active,
                    title = notificationsTitle,
                    onClick = {
                        with(context) {
                            startActivity(
                                Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                                    .putExtra(Settings.EXTRA_CHANNEL_ID, channelId))
                        }
                    })
            }

            // about
            item {
                LinkSettingItem(
                    icon = R.drawable.browser,
                    title = aboutTitle,
                    onClick = { uriHandler.openUri(aboutUrl) })
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