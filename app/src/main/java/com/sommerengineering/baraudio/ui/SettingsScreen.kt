package com.sommerengineering.baraudio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel = koinViewModel()) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(16.dp)) {
            SettingSwitchItem(
                icon = android.R.drawable.stat_sys_speakerphone,
                title = R.string.queue_behavior_title,
                description = R.string.queue_behaivior_description,
                state = viewModel.isQueueFlush.collectAsState(),
                onClick = { viewModel.setIsQueueFlush(it) })
        }
    }
}