package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.rowVerticalPadding
import com.sommerengineering.baraudio.uitls.settingsIconSize

@Composable
fun SwitchItem(
    iconRes: Int,
    title: String,
    description: String? = null,
    iconTint: Color? = null,
    titleColor: Color? = null,
    descriptionColor: Color? = null,
    content: @Composable () -> Unit) {

    Surface {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(rowMinHeight)
                    .padding(rowHorizontalPadding, rowVerticalPadding),
                verticalAlignment = Alignment.CenterVertically) {


                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {

                    // icon
                    Icon(
                        modifier = Modifier.size(settingsIconSize),
                        painter = painterResource(iconRes),
                        tint = iconTint ?: LocalContentColor.current,
                        contentDescription = null)
                    Spacer(Modifier.width(rowIconPadding))

                    // title and description
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            color = titleColor ?: LocalContentColor.current)
                        description?.let {
                            Text(
                                modifier = Modifier.padding(top = 4.dp),
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = descriptionColor ?: LocalContentColor.current.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                // switch
                Spacer(Modifier.width(rowIconPadding))
                content()
            }
        }
    }
}