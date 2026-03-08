package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun GroupHeaderItem(
    viewModel: MainViewModel,
    origin: MessageOrigin,
    messageCount: Int,
    isExpanded: Boolean,
    isShowDivider: Boolean,
    onExpand: () -> Unit) {

    val isDarkMode = viewModel.isDarkMode

    // extract attributes from origin
    val displayName = origin.displayName
    val description = origin.description
    val style = origin.style(isDarkMode)

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowMinHeight)
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
                    .background(style.primary))
            Spacer(Modifier.width(rowIconPadding))

            // display name and description
            Column(
                modifier = Modifier.weight(1f).height(rowMinHeight),
                verticalArrangement = Arrangement.Center) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = style.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = style.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
            }

            // message count
            Spacer(modifier = Modifier.width(rowIconPadding))
            Text(
                text = messageCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = style.accent)

            // chevron with rotation
            val rotation by animateFloatAsState(if (isExpanded) 90f else 0f)
            Icon(
                painter = painterResource(R.drawable.chevron),
                contentDescription = null,
                tint = style.primary,
                modifier = Modifier.rotate(rotation))
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

