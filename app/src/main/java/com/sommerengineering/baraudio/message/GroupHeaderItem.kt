package com.sommerengineering.baraudio.message

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.theme.timestampTextStyle
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding
import com.sommerengineering.baraudio.uitls.settingsRowMinHeight

@Composable
fun GroupHeaderItem(
    origin: MessageOrigin,
    messageCount: Int,
    isExpanded: Boolean,
    isShowDivider: Boolean,
    onExpand: () -> Unit) {

    // extract attributes from origin
    val displayName = origin.displayName
    val description = origin.description
    val style = origin.style

    Column {

        Box {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(settingsRowMinHeight) // required for accent bar
                    .combinedClickable(onClick = { onExpand() })
                    .background(style.surface)
                    .padding(horizontal = rowHorizontalPadding),
                verticalAlignment = Alignment.CenterVertically) {

                // accent bar
                Box(
                    Modifier
                        .width(rowAccentWidth)
                        .fillMaxHeight()
                        .padding(vertical = rowVerticalPadding)
                        .clip(RoundedCornerShape(3.dp))
                        .background(style.primary.copy(alpha = 0.6f)))
                Spacer(Modifier.width(rowIconPadding))

                // display name and description
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = rowVerticalPadding),
                    verticalArrangement = Arrangement.Center) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = style.text)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }

                // message count
                Spacer(modifier = Modifier.width(rowIconPadding))
                Text(
                    text = messageCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f))

                // chevron with rotation
                val rotation by animateFloatAsState(if (isExpanded) 90f else 0f)
                Icon(
                    painter = painterResource(R.drawable.chevron),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                    modifier = Modifier.rotate(rotation))
            }

            // rail connector
            if (isExpanded) {
                val railWidth = rowAccentWidth / 2
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = rowHorizontalPadding + (rowAccentWidth - railWidth) / 2)
                        .width(railWidth)
                        .height(rowVerticalPadding + railWidth / 2)
                        .clip(RoundedCornerShape(
                            topStart = railWidth / 2,
                            topEnd = railWidth / 2,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ))
                        .background(style.primary.copy(alpha = 0.4f)))
            }
        }

        // divider between rows
        if (isShowDivider) {
            HorizontalDivider(
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f),
                modifier = Modifier.fillMaxWidth())
        }
    }
}

