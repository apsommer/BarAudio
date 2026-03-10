package com.sommerengineering.baraudio.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun LinearRail(color: Color) {

    Box(Modifier
            .width(rowAccentWidth)
            .fillMaxHeight()
            .padding(vertical = rowVerticalPadding)
            .clip(RoundedCornerShape(3.dp))
            .background(color))

    Spacer(Modifier.width(rowIconPadding))
}

@Composable
fun GroupedRail(color: Color) {

    Box(Modifier
        .width(rowAccentWidth)
        .fillMaxHeight()) {

        Box(Modifier
            .align(Alignment.Center)
            .width(rowAccentWidth / 2)
            .fillMaxHeight()
            .background(color.copy(0.6f))) }

    Spacer(Modifier.width(rowIconPadding))
}