package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.logMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {

    // todo temp
    val interactionSource = remember { MutableInteractionSource() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {

            delay(1000)
            logMessage("press this! ${this.hashCode()}")

            val press = PressInteraction.Press(Offset(0f,0f))
            interactionSource.emit(press)

            delay(1000)

            val release = PressInteraction.Release(press)
            interactionSource.emit(release)
        }
    }

    // toggle row clickable
    val modifier =
        if (onClick != null) Modifier.clickable { onClick() }
        else Modifier.clickable(enabled = false, onClick = { })

    Surface {
        Column {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.padding(24.dp),
                        painter = painterResource(icon),
                        contentDescription = null)
                    Column {
                        Text(
                            text = stringResource(title),
                            style = MaterialTheme.typography.titleMedium)
                        description?.let {
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                content()
            }
        }
    }
}