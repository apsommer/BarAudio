package com.sommerengineering.baraudio.message

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.sommerengineering.baraudio.source.MessageOrigin

data class MessageItemStyle(
    val primary: Color,
    val accent: Color,
    val surface: Color,
    val text: Color,
    val iconRes: Int?,
    val iconText: String? = null
)

@Composable
fun resolveMessageStyle(
    origin: MessageOrigin,
    isDarkMode: Boolean) = when (origin) {

    is MessageOrigin.BroadcastStream -> origin.asset.style(isDarkMode)
    is MessageOrigin.UserSignal -> origin.source.style(isDarkMode)
}

fun buildStyledMessage(
    text: String,
    colorScheme: ColorScheme,
    isShowAsset: Boolean): AnnotatedString {

    // (Asset) • Event • Variable message that may include numbers

    val slots = text.split(" • ")

    return buildAnnotatedString {

        slots.forEachIndexed { index, part ->

            // dim '•' character
            if (index > 0) {
                withStyle(SpanStyle(colorScheme.onSurface.copy(0.4f))) {
                    append(" • ")
                }
            }

            val style = when {

                // first visible token (asset in linear, event in grouped)
                index == 0 -> SpanStyle(
                    fontWeight = FontWeight.SemiBold)

                // event (second token if asset shown)
                index == 1 && isShowAsset -> SpanStyle(
                    fontWeight = FontWeight.Medium)

                // highlight numbers
                part.any { it.isDigit() } -> SpanStyle(
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.primary)

                // dim slightly long phrases
                part.length > 18 -> SpanStyle(
                    color = colorScheme.onSurface.copy(0.75f))

                else -> SpanStyle()
            }

            withStyle(style) { append(part) }
        }
    }
}