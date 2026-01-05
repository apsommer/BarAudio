package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.settingsIconSize

@Composable
fun SwitchSettingItem(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    content: @Composable () -> Unit) {

    Surface {
        Column {
            Row(
                modifier = Modifier
                    .padding(end = edgePadding),
                verticalAlignment = Alignment.CenterVertically) {

                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .padding(edgePadding)) {
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
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp),
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }

                Spacer(
                    modifier = Modifier
                        .width(edgePadding))

                content()
            }
        }
    }
}