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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.rowVerticalPadding
import com.sommerengineering.baraudio.uitls.settingsIconSize

@Composable
fun LinkSettingItem(
    iconRes: Int,
    title: String,
    onClick: () -> Unit) {

    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(rowMinHeight)
                .padding(
                    start = rowHorizontalPadding + 4.dp,
                    end = rowHorizontalPadding,
                    top = rowVerticalPadding,
                    bottom = rowVerticalPadding)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                modifier = Modifier.size(settingsIconSize),
                painter = painterResource(iconRes),
                contentDescription = null)
            Spacer(Modifier.width(rowIconPadding + 4.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}