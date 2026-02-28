package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.settingsIconSize

@Composable
fun DividerItem(
    text: String) {

    val leadingLineWidth = rowIconPadding + (settingsIconSize / 2)

    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(rowMinHeight),
            verticalAlignment = Alignment.CenterVertically) {

            // line
            HorizontalDivider(
                modifier = Modifier.width(leadingLineWidth),
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.width(rowHorizontalPadding))

            // text
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.width(rowHorizontalPadding))

            // line                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}