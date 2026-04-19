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
import com.sommerengineering.baraudio.uitls.descriptionAlpha
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding
import com.sommerengineering.baraudio.uitls.settingsIconSize
import com.sommerengineering.baraudio.uitls.settingsRowMinHeight
import com.sommerengineering.baraudio.uitls.speedTitle

@Composable
fun SliderItem(
    iconRes: Int,
    title: String,
    description: String,
    content: @Composable () -> Unit) {

    Surface {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(settingsRowMinHeight)
                    .padding(
                        start = rowHorizontalPadding + 4.dp,
                        end = rowHorizontalPadding,
                        top = rowVerticalPadding,
                        bottom = rowVerticalPadding),
                verticalAlignment = Alignment.CenterVertically) {

                Row(
                    verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        modifier = Modifier.size(settingsIconSize),
                        painter = painterResource(iconRes),
                        contentDescription = null)
                    Spacer(Modifier.width(rowIconPadding + 4.dp))

                    Column {

                        Box {

                            // invisible "Speed" title aligns start position of sequential sliders
                            Text(
                                text = speedTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Transparent)

                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium)
                        }

                        // description
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(descriptionAlpha),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }

                Spacer(Modifier.width(rowIconPadding))

                content()
            }
        }
    }
}