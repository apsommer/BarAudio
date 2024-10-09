package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.ui.theme.AppTheme

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