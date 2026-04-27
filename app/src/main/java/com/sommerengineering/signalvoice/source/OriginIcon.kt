package com.sommerengineering.signalvoice.source

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.signalvoice.theme.fontFamily
import com.sommerengineering.signalvoice.uitls.assetIconSize
import com.sommerengineering.signalvoice.uitls.settingsIconSize

@Composable
fun OriginIcon(
    messageOrigin: MessageOrigin,
    isSettings: Boolean = false
) {

    val style = messageOrigin.style

    // de-emphasize settings presentation
    val size =
        if (isSettings) settingsIconSize
        else assetIconSize
    val textIconBackground =
        if (isSettings) style.primary.copy(alpha = 0.85f)
        else style.primary.copy(alpha = 0.85f)
    val borderColor =
        if (isSettings) style.accent.copy(alpha = 0.4f)
        else style.accent.copy(alpha = 0.4f)

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(textIconBackground)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        // drawable icon
        if (style.iconRes != null) {
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize()
            )
        }

        // text icon (NQ: "100", ES: "500", ...)
        else {

            val iconText = style.iconText ?: return@Box

            // adjust text spacing
            val fontSize = if (isSettings) 11.sp else 12.sp
            val letterSpacing = if (iconText == "100") (-0.2).sp else (-0.5).sp
            val modifier = when (iconText) {
                "100" -> Modifier.offset(x = (-0.5).dp)
                "500" -> Modifier.offset(x = 0.3.dp)
                else -> Modifier
            }

            Text(
                text = iconText,
                color = style.text,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                modifier = modifier
            )
        }
    }
}



