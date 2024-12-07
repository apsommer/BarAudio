package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

// todo do this without experimental optin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar(
    onSettingsClick: () -> Unit,
    messages: SnapshotStateList<Message>) {

    return CenterAlignedTopAppBar(

        modifier = Modifier
            .padding(8.dp),

        // open settings drawer
        navigationIcon = {
            IconButton(
                onClick = { onSettingsClick() }) {
                Icon(
                    painter = painterResource(R.drawable.menu),
                    contentDescription = null)
            }
        },

        // logo
        title = {
            Icon(
                modifier = Modifier
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
                    painter = painterResource(R.drawable.sweep),
                    contentDescription = null)
            }
        })
}