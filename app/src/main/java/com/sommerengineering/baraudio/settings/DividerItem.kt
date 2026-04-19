package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.settingsIconSize

@Composable
fun DividerItem(
    text: String) {

    val rowHeight = 82.dp
    val leadingLineWidth = rowIconPadding + (settingsIconSize / 2)
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(rowHeight),
            verticalAlignment = Alignment.CenterVertically) {

            // line
            HorizontalDivider(
                modifier = Modifier.width(leadingLineWidth),
                thickness = dividerThickness,
                color = color)
            Spacer(Modifier.width(rowHorizontalPadding))

            // text
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = color)
            Spacer(Modifier.width(rowHorizontalPadding))

            // line
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = dividerThickness,
                color = color)
        }
    }
}