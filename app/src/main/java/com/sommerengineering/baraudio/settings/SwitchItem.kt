package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.descriptionAlpha
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowHeight
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding

@Composable
fun SwitchItem(
    icon: @Composable () -> Unit,
    title: String,
    description: String? = null,
    titleColor: Color? = null,
    descriptionColor: Color? = null,
    content: @Composable () -> Unit) {

    Surface {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(rowHeight)
                    .padding(
                        start = rowHorizontalPadding + 4.dp,
                        end = rowHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically) {

                    // icon
                    icon()
                    Spacer(Modifier.width(rowIconPadding + 4.dp))

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
                                color = descriptionColor ?: LocalContentColor.current.copy(descriptionAlpha),
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