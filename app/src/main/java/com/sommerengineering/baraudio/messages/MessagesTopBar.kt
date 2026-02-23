package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.sommerengineering.baraudio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar(
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit) {

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
                    .fillMaxWidth(0.75f)
                    .padding(8.dp),
                painter = painterResource(R.drawable.logo_banner),
                contentDescription = null)
        },

        // feed mode: linear/grouped
        actions = {
            IconButton(
                onClick = { onToggleFeedMode() }) {
                Icon(
                    painter = painterResource(R.drawable.delete_sweep),
                    contentDescription = null)
            }
        })
}