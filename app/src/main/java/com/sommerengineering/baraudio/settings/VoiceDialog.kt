package com.sommerengineering.baraudio.settings

import android.speech.tts.Voice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun VoiceDialog(
    voices: List<Voice>,
    onItemSelected: () -> Unit) {

    Dialog (
        onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn {
                items(voices) { voice ->
                    Text(
                        text = voice.name,
                        modifier = Modifier.clickable { onItemSelected() })
                }
            }
        }
    }
}