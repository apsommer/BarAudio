package com.sommerengineering.baraudio.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

@Composable
fun PageIndicator(
    pageNumber: Int,
    totalPages: Int,
    modifier: Modifier) {

    // page indicator
    Row(
        modifier = modifier) {

        for (i in 0..< totalPages) {

            val imageId =
                if (i == pageNumber) R.drawable.indicator_filled
                else R.drawable.indicator_open

            Icon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(12.dp),
                tint = MaterialTheme.colorScheme.outline,
                painter = painterResource(imageId),
                contentDescription = null)
        }
    }
}