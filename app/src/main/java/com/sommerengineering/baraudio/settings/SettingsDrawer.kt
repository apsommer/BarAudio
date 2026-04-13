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
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.btcAsset
import com.sommerengineering.baraudio.source.esAsset
import com.sommerengineering.baraudio.source.gcAsset
import com.sommerengineering.baraudio.source.nqAsset
import com.sommerengineering.baraudio.source.siAsset
import com.sommerengineering.baraudio.source.znAsset
import com.sommerengineering.baraudio.uitls.customDividerTitle
import com.sommerengineering.baraudio.uitls.streamsDividerTitle
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.legalDividerTitle
import com.sommerengineering.baraudio.uitls.manageSubscriptionTitle
import com.sommerengineering.baraudio.uitls.pitchChangeUtterance
import com.sommerengineering.baraudio.uitls.pitchTitle
import com.sommerengineering.baraudio.uitls.premiumDividerTitle
import com.sommerengineering.baraudio.uitls.screenTitle
import com.sommerengineering.baraudio.uitls.signOutTitle
import com.sommerengineering.baraudio.uitls.speedChangeUtterance
import com.sommerengineering.baraudio.uitls.speedTitle
import com.sommerengineering.baraudio.uitls.subscriptionUrl
import com.sommerengineering.baraudio.uitls.systemTtsDescription
import com.sommerengineering.baraudio.uitls.systemTtsInstallVoicesAction
import com.sommerengineering.baraudio.uitls.systemTtsTitle
import com.sommerengineering.baraudio.uitls.uiDividerTitle
import com.sommerengineering.baraudio.uitls.uiModeTitle
import com.sommerengineering.baraudio.uitls.voiceDividerTitle
import com.sommerengineering.baraudio.uitls.voiceTitle
import com.sommerengineering.baraudio.uitls.customDescription
import com.sommerengineering.baraudio.uitls.customTitle
import com.sommerengineering.baraudio.uitls.manageSubscriptionDescription
import com.sommerengineering.baraudio.uitls.signOutDescription

@Composable
fun SettingsDrawer(
    viewModel: MainViewModel,
    onSignOut: () -> Unit,
    onLaunchSetupOnboarding: () -> Unit) {

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val speed = viewModel.speed
    val pitch = viewModel.pitch
    val voiceDescription = viewModel.voiceDescription
    val speedDescription = viewModel.speedDescription
    val pitchDescription = viewModel.pitchDescription

    val isZN = viewModel.isZN
    val isNQ = viewModel.isNQ
    val isBTC = viewModel.isBTC
    val isES = viewModel.isES
    val isGC = viewModel.isGC
    val isSI = viewModel.isSI

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
                DialogItem (
                    iconRes = R.drawable.voice,
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
                SliderItem(
                    iconRes = R.drawable.speed,
                    title = speedTitle,
                    description = speedDescription) {

                        SliderImpl(
                            initPosition = speed,
                            onValueChanged = { viewModel.updateSpeed(it) },
                            onValueChangeFinished = {
                                viewModel.speakUtterance(speedChangeUtterance + it)
                            })
                    }
            }

            // pitch
            item {
                SliderItem(
                    iconRes = R.drawable.pitch,
                    title = pitchTitle,
                    description = pitchDescription) {

                        SliderImpl(
                            initPosition = pitch,
                            onValueChanged = { viewModel.updatePitch(it) },
                            onValueChangeFinished = {
                                viewModel.speakUtterance(pitchChangeUtterance + it)
                            })
                        }
            }

            // system tts settings
            item {
                LinkItem(
                    iconRes = R.drawable.settings,
                    title = systemTtsTitle,
                    description = systemTtsDescription,
                    onClick = {
                        with(context) {
                            startActivity(
                                Intent(systemTtsInstallVoicesAction)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        }
                    })
            }

            // divider
            item {
                DividerItem(streamsDividerTitle)
            }

            // stream ZN
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(znAsset),
                    isDarkMode = isDarkMode,
                    isStream = isZN,
                    updateStream = { viewModel.updateZN(it) })
            }

            // stream NQ
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(nqAsset),
                    isDarkMode = isDarkMode,
                    isStream = isNQ,
                    updateStream = { viewModel.updateNQ(it) })
            }

            // stream BTC
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(btcAsset),
                    isDarkMode = isDarkMode,
                    isStream = isBTC,
                    updateStream = { viewModel.updateBTC(it) })
            }

            // divider
            item {
                DividerItem(premiumDividerTitle)
            }

            // stream ES
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(esAsset),
                    isDarkMode = isDarkMode,
                    isStream = isES,
                    updateStream = { viewModel.updateES(it) })
            }

            // stream GC
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(gcAsset),
                    isDarkMode = isDarkMode,
                    isStream = isGC,
                    updateStream = { viewModel.updateGC(it) })
            }

            // stream SI
            item {
                StreamSwitchItem(
                    messageOrigin = MessageOrigin.BroadcastStream(siAsset),
                    isDarkMode = isDarkMode,
                    isStream = isSI,
                    updateStream = { viewModel.updateSI(it) })
            }

            // divider
            item {
                DividerItem(customDividerTitle)
            }

            // webhook
            item {
                DialogItem(
                    iconRes = R.drawable.webhook,
                    title = customTitle,
                    description = customDescription,
                    onClick = onLaunchSetupOnboarding) {
                    Icon(
                        painter = painterResource(R.drawable.chevron),
                        contentDescription = null)
                }
            }

            // divider
            item {
                DividerItem(uiDividerTitle)
            }

            // full screen
            item {
                SwitchItem(
                    iconRes = R.drawable.fullscreen,
                    iconTint = MaterialTheme.colorScheme.onSurface,
                    title = screenTitle,
                    description = fullScreenDescription) {

                    Switch(
                        checked = isFullScreen,
                        onCheckedChange = { viewModel.updateFullScreen(it) })
                }
            }

            // theme
            item {
                SwitchItem(
                    iconRes = R.drawable.contrast,
                    iconTint = MaterialTheme.colorScheme.onSurface,
                    title = uiModeTitle,
                    description = uiModeDescription) {

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.updateDarkMode(it) })
                }
            }

            // divider
            item {
                DividerItem(legalDividerTitle)
            }

            // manage subscription
            item {
                LinkItem(
                    iconRes = R.drawable.manage_subscription,
                    title = manageSubscriptionTitle,
                    description = manageSubscriptionDescription,
                    onClick = { uriHandler.openUri(subscriptionUrl) })
            }

            // sign-out
            item {
                LinkItem(
                    iconRes = R.drawable.sign_out,
                    iconTint = true,
                    title = signOutTitle,
                    description = signOutDescription,
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