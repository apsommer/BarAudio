package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.uitls.rowAccentWidth

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    message: Message,
    isShowDivider: Boolean,
    modifier: Modifier) {

    val feedMode = viewModel.feedMode

    if (feedMode == FeedMode.Linear) {
        LinearMessageRow(
            viewModel = viewModel,
            message = message,
            isShowDivider = isShowDivider,
            modifier = modifier)
        return
    }

    GroupedMessageRow(
        viewModel = viewModel,
        message = message,
        isShowDivider = isShowDivider,
        modifier = modifier)
}

@Composable
fun Rail(color: Color) {
    Box(Modifier
        .width(rowAccentWidth / 2)
        .fillMaxHeight()
        .background(color.copy(0.6f)))
}