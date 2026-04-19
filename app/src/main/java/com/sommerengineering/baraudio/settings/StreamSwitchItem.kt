package com.sommerengineering.baraudio.settings

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.OriginIcon
import com.sommerengineering.baraudio.uitls.assetIconSize
import com.sommerengineering.baraudio.uitls.descriptionAlpha
import com.sommerengineering.baraudio.uitls.settingsIconSize

@Composable
fun StreamSwitchItem(
    messageOrigin: MessageOrigin,
    isStream: Boolean,
    updateStream: (Boolean) -> Unit) {

    val style = messageOrigin.style

    SwitchItem(
        icon = {
            OriginIcon(
                messageOrigin = messageOrigin,
                isSettings = true)
        },
        title = messageOrigin.displayName,
        description = messageOrigin.signalDescription,
        titleColor = if (isStream) style.primary else null,
        descriptionColor = if (isStream) style.primary.copy(descriptionAlpha) else null) {
        Switch(
            checked = isStream,
            onCheckedChange = { updateStream(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = style.primary,
                checkedTrackColor = style.primary.copy(alpha = 0.4f)))
    }
}