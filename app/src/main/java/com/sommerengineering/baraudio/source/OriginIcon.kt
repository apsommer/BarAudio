package com.sommerengineering.baraudio.source

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.baraudio.theme.fontFamily
import com.sommerengineering.baraudio.uitls.assetIconSize

@Composable
fun OriginIcon(
    messageOrigin: MessageOrigin,
    isDark: Boolean) {

    val style = messageOrigin.style(isDark)

    Box(
        modifier = Modifier
            .size(assetIconSize)
            .clip(CircleShape)
            .background(style.primary)
            .border(
                width = 1.dp,
                color = style.accent.copy(alpha = 0.6f),
                shape = CircleShape),
        contentAlignment = Alignment.Center) {

        // drawable icon
        if (style.iconRes != null) {
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.fillMaxSize())
        }

        // text icon
        else {

            val iconText = style.iconText ?: return@Box

            Text(
                text = iconText,
                color = style.text,
                fontSize =
                    if (iconText == "₿") 19.sp
                    else 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                letterSpacing =
                    if (iconText == "100") (-0.2).sp
                    else (-0.5).sp,
                modifier =
                    when (iconText) {
                        "₿" -> Modifier.rotate(-12f)
                        "100" -> Modifier.offset(x = (-0.5).dp)
                        else -> Modifier
                    })
        }
    }
}

