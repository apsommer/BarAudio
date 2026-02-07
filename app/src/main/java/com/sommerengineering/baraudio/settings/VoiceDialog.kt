package com.sommerengineering.baraudio.settings

import android.speech.tts.Voice
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.edgePadding

@Composable
fun VoiceDialog(
    viewModel: MainViewModel,
    onItemSelected: (Voice) -> Unit,
    onDismiss: () -> Unit) {

    val listState = rememberLazyListState()
    val voices = viewModel.voices

    // scroll to current voice
    LaunchedEffect(viewModel.voiceIndex) {
        listState.scrollToItem(
            viewModel.voiceIndex)
    }

    Dialog (
        onDismissRequest = { onDismiss() }) {

        Surface(
            modifier = Modifier
                .padding(vertical = edgePadding)
                .clip(RoundedCornerShape(16.dp))) {
            Column {

                // title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(edgePadding),
                    horizontalArrangement = Arrangement.Center) {

                    Text(
                        text = "Voices",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center)
                }

                // list
                LazyColumn(
                    state = listState) {

                    items(voices) {
                        VoiceItem(
                            viewModel = viewModel,
                            voice = it,
                            onItemSelected = onItemSelected
                        )
                    }
                }
            }
        }
    }
}
