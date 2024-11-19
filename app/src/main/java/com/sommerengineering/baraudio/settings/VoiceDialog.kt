package com.sommerengineering.baraudio.settings

import android.speech.tts.Voice
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sommerengineering.baraudio.beautifyTimestamp

@Composable
fun VoiceDialog(
    voices: List<Voice>,
    onItemSelected: (Voice) -> Unit,
    onDismiss: () -> Unit) {

    Dialog (
        onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.padding(vertical = 36.dp)
        ) {
            LazyColumn {
                items(voices) { voice ->
                    VoiceItem(
                        voice = voice,
                        onItemSelected = onItemSelected
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceItem(
    voice: Voice,
    onItemSelected: (Voice) -> Unit) {

    Surface(
        modifier = Modifier.clickable { onItemSelected(voice) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = voice.name,
                style = MaterialTheme.typography.bodyLarge)
        }
        HorizontalDivider()
    }
}