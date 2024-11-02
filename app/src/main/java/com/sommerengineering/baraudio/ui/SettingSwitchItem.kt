package com.sommerengineering.baraudio.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingSwitchItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes description: Int,
    state: State<Boolean>,
    onClick: (Boolean) -> Unit
) {

    Surface {
        Column {
            Row(
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
                            text = stringResource(description),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
                Switch(
                    modifier = Modifier.padding(24.dp),
                    checked = state.value,
                    onCheckedChange = { onClick(it) })
            }
        }
    }
}