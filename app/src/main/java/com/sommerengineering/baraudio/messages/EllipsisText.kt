package com.sommerengineering.baraudio.messages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun EllipsisText(
    text: String,
    style: TextStyle,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier) {

    // if word is longer than one line
    // ensure ellipse is placed at a word breakpoint
    // and preceded by a space character
    // "Apples and banan..." -> "Apples and ..."

    var displayText by remember(text) { mutableStateOf(text) }

    Text(
        text = displayText,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        color = color,
        modifier = modifier,
        onTextLayout = { layout ->

            if (layout.hasVisualOverflow && displayText == text) {

                val visibleEnd = layout.getLineEnd(0, visibleEnd = true)

                val isCuttingWord =
                    visibleEnd > 0 &&
                        visibleEnd < text.length &&
                        text[visibleEnd] != ' ' &&
                        text[visibleEnd - 1] != ' '

                val safeCut =
                    if (isCuttingWord) {
                        text.lastIndexOf(' ', visibleEnd)
                            .takeIf { it > 0 }
                                ?: visibleEnd
                    }
                    else { visibleEnd }

                displayText = text.substring(0, safeCut).trimEnd() + " ..."
            }
        })
}