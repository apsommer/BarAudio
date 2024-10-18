package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.theme.AppTheme

sealed class AlertsState {
    object Loading : AlertsState()
    data class Success(val alerts: List<String>) : AlertsState()
    data class Error(val message: String) : AlertsState()
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    // todo debug test
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    Scaffold(
        topBar = {
            // todo profile image top right
        },
        floatingActionButton = {
            // todo plus button
        }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            items(getAlerts()) { alert ->
                AlertItem(alert)
                HorizontalDivider()
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "dark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "light"
)
@Composable
fun PreviewAlertsScreen() {
    AppTheme {
        AlertsScreen()
    }
}