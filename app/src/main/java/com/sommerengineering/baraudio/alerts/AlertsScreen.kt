package com.sommerengineering.baraudio.alerts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    Scaffold(
        topBar = {
            // todo profile image top right
        }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            // todo add alerts
        }
    }
}