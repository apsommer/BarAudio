package com.sommerengineering.signalvoice.settings

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import com.sommerengineering.signalvoice.source.MessageOrigin
import com.sommerengineering.signalvoice.source.OriginIcon
import com.sommerengineering.signalvoice.uitls.streamDescriptionAlpha

@Composable
fun StreamSwitchItem(
    messageOrigin: MessageOrigin,
    isStream: Boolean,
    updateStream: (Boolean) -> Unit
) {

    val style = messageOrigin.style

    SwitchItem(
        icon = {
            OriginIcon(
                messageOrigin = messageOrigin,
                isSettings = true
            )
        },
        title = messageOrigin.displayName,
        description = messageOrigin.signalDescription,
        titleColor = if (isStream) style.primary else null,
        descriptionColor = if (isStream) style.primary.copy(streamDescriptionAlpha) else null
    ) {
        Switch(
            checked = isStream,
            onCheckedChange = { updateStream(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = style.primary,
                checkedTrackColor = style.primary.copy(alpha = 0.4f)
            )
        )
    }
}