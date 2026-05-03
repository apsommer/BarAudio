package com.sommerengineering.signalvoice.message

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.sommerengineering.signalvoice.source.MessageOrigin
import com.sommerengineering.signalvoice.source.assetDisplayNames

data class MessageItemStyle(
    val primary: Color,
    val accent: Color,
    val surface: Color,
    val text: Color,
    val iconRes: Int?,
    val iconText: String? = null
)

@Composable // todo doesn't need to be composable, raw hex palletes
fun resolveMessageStyle(
    origin: MessageOrigin
) = when (origin) {

    is MessageOrigin.BroadcastStream -> origin.asset.style
    is MessageOrigin.UserSignal -> origin.source.style
}

@Composable
fun buildStyledMessage(
    displayText: String,
): AnnotatedString {

    // (Asset) • Event • Variable message that may include numbers • Variable message that may include numbers

    // split message into parts
    val parts = displayText.split("•").map { it.trim() }

    // determine if first part is asset name
    val isShowAsset = parts.first() in assetDisplayNames

    return buildAnnotatedString {

        parts.forEachIndexed { index, part ->

            // linear mode: asset prepended, grouped mode: raw message, first part is event
            val isAsset = index == 0 && isShowAsset
            val isEvent =
                if (isShowAsset) index == 1
                else index == 0

            // dim '•' character
            if (index > 0) {
                withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface.copy(0.25f))) {
                    append(" • ")
                }
            }

            val style = when {

                // bold asset (if shown)
                isAsset -> SpanStyle(
                    fontWeight = FontWeight.SemiBold
                )

                // medium event
                isEvent -> SpanStyle(
                    fontWeight = FontWeight.Medium
                )

                // highlight numbers
                part.any { it.isDigit() } -> SpanStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                )

                // variable part(s)
                index >= (if (isShowAsset) 2 else 1) -> SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(0.85f)
                )

                else -> SpanStyle()
            }

            withStyle(style) { append(part) }
        }
    }
}