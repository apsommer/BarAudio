package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.theme.AppTheme

sealed class AlertsState {
    companion object { val route = "AlertsScreen" }
    object Loading : AlertsState()
    data class Success(val alerts: List<String>) : AlertsState()
    data class Error(val message: String) : AlertsState()
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    Scaffold(
        topBar = {
            // todo profile image top right
        },
        floatingActionButton = {
            // todo plus button
        }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            items(items = getAlerts()) { alert ->
                AlertItem(alert)
            }
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "dark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "light"
)
@Composable
fun PreviewDarkLight() {
    AppTheme {
        AlertsScreen()
    }
}