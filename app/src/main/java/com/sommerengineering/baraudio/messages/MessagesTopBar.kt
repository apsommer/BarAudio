package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
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

        modifier = Modifier.padding(horizontal = 8.dp),

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
            Box {

                Image(
                    painter = painterResource(R.drawable.appbar),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer {
                        scaleX = 0.6f
                        scaleY = 0.6f
                    })

                // scrim overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background.copy(logoAlpha)))
            }
        },

        // feed mode
        actions = {
            IconButton(
                onClick = { onToggleFeedMode() }) {
                Icon(
                    painter = painterResource(feedModeIcon),
                    contentDescription = null) }})
}