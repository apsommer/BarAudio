package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.logoAlpha
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun MessagesTopBar(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit,
    onToggleMute: () -> Unit) {

    val feedMode = viewModel.feedMode
    val feedModeIcon =
        if (feedMode == FeedMode.Linear) R.drawable.group
        else R.drawable.ungroup

    val isMute = viewModel.isMute
    val muteIcon =
        if (isMute) R.drawable.volume_off
        else R.drawable.volume_on

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = rowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically) {

        // settings drawer
        Icon(
            modifier = Modifier
                .rotate(180f)
                .clickable { onSettingsClick() }
                .padding(horizontal = rowHorizontalPadding),
            painter = painterResource(R.drawable.menu_open),
            contentDescription = null)

        // logo
        Box(Modifier.weight(1f)) {
            ScrimImage(
                iconRes = R.drawable.appbar,
                alpha = logoAlpha,
                modifier = Modifier
                    .align(Alignment.CenterStart)
//                    .padding(vertical = rowHorizontalPadding)
            )
        }

        // feed mode
        Icon(
            modifier = Modifier
                .clickable { onToggleFeedMode() }
                .padding(start = rowHorizontalPadding),
            painter = painterResource(feedModeIcon),
            contentDescription = null)

        // mute
        Icon(
            modifier = Modifier
                .clickable { onToggleMute() }
                .padding(horizontal = rowHorizontalPadding),
            painter = painterResource(muteIcon),
            contentDescription = null)
    }
}