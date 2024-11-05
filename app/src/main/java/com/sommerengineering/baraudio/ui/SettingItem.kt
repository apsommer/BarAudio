package com.sommerengineering.baraudio.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    description: String,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    // toggle row clickable
    val modifier =
        if (onClick != null) Modifier.clickable { onClick() }
        else Modifier.clickable(enabled = false, onClick = { })

    Surface {
        Column {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(24.dp),
                        painter = painterResource(icon),
                        contentDescription = null)
                    Column {
                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
                content()
            }
        }
    }
}