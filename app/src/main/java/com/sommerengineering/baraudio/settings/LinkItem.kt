package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.descriptionAlpha
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.settingsIconSize
import com.sommerengineering.baraudio.uitls.rowHeight
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding

@Composable
fun LinkItem(
    iconRes: Int,
    iconTint: Boolean = false,
    title: String,
    description: String,
    onClick: () -> Unit) {

    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(rowHeight)
                .clickable { onClick() }
                .padding(
                    start = rowHorizontalPadding + 4.dp,
                    end = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                modifier = Modifier.size(settingsIconSize),
                painter = painterResource(iconRes),
                tint =
                    if (iconTint) LocalContentColor.current.copy(descriptionAlpha)
                    else LocalContentColor.current,
                contentDescription = null)
            Spacer(Modifier.width(rowIconPadding + 4.dp))

            // title and description
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LocalContentColor.current)
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(descriptionAlpha),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }
        }
    }
}