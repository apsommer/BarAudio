package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.logMessage

@Composable
fun DialogSettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    description: String,
    content: @Composable () -> Unit) {

    Surface {
        Column {
            Row(
                modifier = Modifier
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .padding(24.dp)) {
                        Icon(
                            modifier = Modifier
                                .size(24.dp),
                            painter = painterResource(icon),
                            contentDescription = null)
                    }

                    Column {
                        Text(
                            text = stringResource(title),
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
                        .width(24.dp))

                content()
            }
        }
    }
}