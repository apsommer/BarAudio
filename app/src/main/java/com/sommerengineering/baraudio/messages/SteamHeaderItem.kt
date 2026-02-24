package com.sommerengineering.baraudio.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Surface
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
import com.sommerengineering.baraudio.assets.assetMap
import com.sommerengineering.baraudio.assets.resolveAsset
import com.sommerengineering.baraudio.assets.resolveAssetStyle

@Composable
fun StreamHeaderItem(
    viewModel: MainViewModel,
    origin: String,
    messageCount: Int,
    isExpanded: Boolean,
    onExpand: () -> Unit) {

    // resolve asset
    val style = resolveAssetStyle(origin, viewModel.isDarkMode)
    val asset = resolveAsset(origin)

    Surface {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = { onExpand() })
                .height(IntrinsicSize.Min)
                .background(style.surface)
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically) {

            // accent bar
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(style.primary))
            Spacer(Modifier.width(16.dp))

            // display name and description
            Column(Modifier.weight(1f)) {
                Text(
                    text = asset.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = style.text)
                Spacer(Modifier.height(2.dp))
                Text(
                    text = asset.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = style.accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) }

            // message count
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = messageCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = style.accent)
            Spacer(Modifier.width(8.dp))

            // chevron with rotation
            val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)
            Icon(
                painter = painterResource(R.drawable.chevron),
                contentDescription = null,
                tint = style.primary,
                modifier = Modifier.rotate(rotation))
        }

        // divider between rows
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.fillMaxWidth())
    }
}
