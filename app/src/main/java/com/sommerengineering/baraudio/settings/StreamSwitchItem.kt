package com.sommerengineering.baraudio.settings

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.sommerengineering.baraudio.source.MessageOrigin

@Composable
fun StreamSwitchItem(
    messageOrigin: MessageOrigin,
    isDarkMode: Boolean,
    isStream: Boolean,
    updateStream: (Boolean) -> Unit) {

    val style = messageOrigin.style(isDarkMode)

    SwitchItem(
        iconRes = style.iconRes,
        title = messageOrigin.settingsTitle(),
        description = messageOrigin.signalDescription,
        iconTint = style.primary,
        titleColor = if (isStream) style.primary else null,
        descriptionColor = if (isStream) style.primary.copy(alpha = 0.7f) else null) {
        Switch(
            checked = isStream,
            onCheckedChange = { updateStream(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = style.primary,
                checkedTrackColor = style.primary.copy(alpha = 0.4f)))
    }
}