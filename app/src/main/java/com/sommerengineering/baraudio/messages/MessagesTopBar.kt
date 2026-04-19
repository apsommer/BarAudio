package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.logoAlpha
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit) {

    val feedMode = viewModel.feedMode
    val feedModeIcon =
        if (feedMode == FeedMode.Linear) R.drawable.group
        else R.drawable.ungroup

    CenterAlignedTopAppBar(

        modifier = Modifier
            .padding(8.dp),

        // settings
        navigationIcon = {
            IconButton(
                onClick = { onSettingsClick() }) {
                Icon(
                    modifier = Modifier
                        .rotate(180f),
                    painter = painterResource(R.drawable.menu_open),
                    contentDescription = null) }},

        // logo
        title = {
            Box { ScrimImage(
                iconRes = R.drawable.appbar,
                alpha = logoAlpha,
                modifier = Modifier.padding(horizontal = 2 * rowHorizontalPadding)) }},

        // feed mode
        actions = {
            IconButton(
                onClick = { onToggleFeedMode() }) {
                Icon(
                    painter = painterResource(feedModeIcon),
                    contentDescription = null) }})
}