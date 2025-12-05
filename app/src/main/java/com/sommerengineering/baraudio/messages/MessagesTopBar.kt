package com.sommerengineering.baraudio.messages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.deleteAllFadeDurationMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar(
    messages: SnapshotStateList<Message>,
    onSettingsClick: () -> Unit) {

    // animate color of "delete all" button
    var isEmpty by remember { mutableStateOf(true) }
    isEmpty = messages.isEmpty()

    CenterAlignedTopAppBar(

        modifier = Modifier
            .padding(8.dp),

        // open settings drawer
        navigationIcon = {
            IconButton(
                onClick = { onSettingsClick() }) {
                Icon(
                    modifier = Modifier
                        .rotate(180f),
                    painter = painterResource(R.drawable.menu_open),
                    contentDescription = null)
            }
        },

        // logo
        title = {
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(8.dp),
                painter = painterResource(R.drawable.logo_banner),
                contentDescription = null)
        },

        // delete all
        actions = {
            IconButton(
                onClick = { deleteAllMessages(messages) },
                enabled = !messages.isEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.done_all),
                    tint = animateColorAsState(
                        targetValue =
                            if (isEmpty) IconButtonDefaults.iconButtonColors().disabledContentColor
                            else IconButtonDefaults.iconButtonColors().contentColor,
                        animationSpec = tween(deleteAllFadeDurationMillis),
                        label = ""
                    ).value,
                    contentDescription = null)
            }
        })
}