package com.sommerengineering.baraudio.settings

import android.speech.tts.Voice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

@Composable
fun VoiceDropdownMenu(
    voices: List<Voice>) {

    var isExpanded by remember { mutableStateOf(false) }

    Icon(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable { isExpanded = !isExpanded},
        painter = painterResource(R.drawable.more_vertical),
        contentDescription = null)
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false }) {
        LazyColumn(
            modifier = Modifier // todo clean up, lazycolumn without defined size not supported in dropdownmenu
                .width(400.dp)
                .height(400.dp)
        ) {
            items(voices) { voice ->
                DropdownMenuItem(
                    text = { Text(voice.name) },
                    onClick = { })
            }
        }
    }
}