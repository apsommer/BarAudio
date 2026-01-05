package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.settingsIconSize

@Composable
fun LinkSettingItem(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit) {

    Surface {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier.padding(edgePadding)) {
                Icon(
                    modifier = Modifier
                        .size(settingsIconSize),
                    painter = painterResource(icon),
                    contentDescription = null)
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}