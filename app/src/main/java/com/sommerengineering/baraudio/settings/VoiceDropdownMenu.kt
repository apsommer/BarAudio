package com.sommerengineering.baraudio.settings

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.logMessage

@Composable
fun VoiceDropdownMenu() {

    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { isExpanded = !isExpanded }) {
        Icon(
            painter = painterResource(R.drawable.more_vertical),
            contentDescription = null)
    }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false }) {

        DropdownMenuItem(
            text = { Text( "English - Male - Australian") },
            onClick = { logMessage("English - Male - Australian") })
        HorizontalDivider()
        DropdownMenuItem(
            text = { Text( "Spanish - Female - Castilian") },
            onClick = { logMessage("Spanish - Female - Castilian") })
        HorizontalDivider()
    }
}