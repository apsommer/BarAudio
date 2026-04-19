package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.logoAlpha
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun MessagesTopBar(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit) {

    val feedMode = viewModel.feedMode
    val feedModeIcon =
        if (feedMode == FeedMode.Linear) R.drawable.group
        else R.drawable.ungroup

    Box(Modifier
        .fillMaxWidth()
//        .height(56.dp)
        .padding(
            horizontal = rowHorizontalPadding,
        )) {

        // settings drawer
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = onSettingsClick) {
            Icon(
                painter = painterResource(R.drawable.menu_open),
                contentDescription = null,
                modifier = Modifier.rotate(180f))
        }

        // logo
        ScrimImage(
            iconRes = R.drawable.appbar,
            alpha = logoAlpha,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = edgePadding)
        )

        // feed mode: linear/grouped
        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onToggleFeedMode) {
            Icon(
                painter = painterResource(feedModeIcon),
                contentDescription = null)
        }
    }
}