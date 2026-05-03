package com.sommerengineering.signalvoice.source

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.Sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.theme.fontFamily
import com.sommerengineering.signalvoice.uitls.assetIconSize
import com.sommerengineering.signalvoice.uitls.settingsIconSize

@Composable
fun OriginIcon(
    origin: MessageOrigin,
    isSettings: Boolean = false,
    onLockedClick: (() -> Unit)? = null
) {

    val style = origin.style

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

    // locked status: show lock badge and make icon clickable
    val isLocked = onLockedClick != null
    val clickableModifier =
        if (isLocked) Modifier.clickable { onLockedClick() }
        else Modifier

    Box(
        modifier = Modifier
            .size(size)
            .then(clickableModifier)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
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
            if (style.iconText != null) {
                TextIcon(
                    text = style.iconText,
                    fontSize = if (isSettings) 11.sp else 12.sp,
                    color = style.text
                )
            }
        }

        if (isLocked) {
            LockBadge(
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun TextIcon(
    text: String,
    fontSize: TextUnit,
    color: Color,
) {

    // adjust text spacing
    val letterSpacing = if (text == "100") (-0.2).sp else (-0.5).sp
    val modifier = when (text) {
        "100" -> Modifier.offset(x = (-0.5).dp)
        "500" -> Modifier.offset(x = 0.3.dp)
        else -> Modifier
    }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        modifier = modifier
    )
}

@Composable
fun LockBadge(modifier: Modifier = Modifier) {

    val badgeSize = 12.dp
    val badgePadding = 2.dp

    // accommodate inherent vector padding from emoji conversion
    val offset = badgeSize * 0.14f + badgePadding

    Box(
        modifier = modifier
            .size(badgeSize)
            .offset(x = offset, y = badgePadding)
    ) {

        // halo
        Box(
            Modifier
                .matchParentSize()
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        )

        // lock
        Icon(
            painter = painterResource(R.drawable.lock),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .padding(badgePadding),
            tint = Color.White.copy(alpha = 0.8f)
        )
    }
}



