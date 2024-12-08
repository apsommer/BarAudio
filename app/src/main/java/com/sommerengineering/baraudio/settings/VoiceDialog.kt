package com.sommerengineering.baraudio.settings

import android.speech.tts.Voice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sommerengineering.baraudio.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun VoiceDialog(
    viewModel: MainViewModel,
    onItemSelected: (Voice) -> Unit,
    onDismiss: () -> Unit) {

    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()

    Dialog (
        onDismissRequest = { onDismiss() }) {

        Surface(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(16.dp))) {
            Column {

                // title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.Center) {

                    Text(
                        text = "Voices",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center)
                }

                // list
                LazyColumn(
                    state = listState) {

                    items(viewModel.voices) {
                        VoiceItem(
                            viewModel = viewModel,
                            voice = it,
                            onItemSelected = onItemSelected
                        )
                    }

                    coroutine.launch {
                        listState.scrollToItem(
                            viewModel.getVoiceIndex())
                    }
                }
            }
        }
    }
}
