package com.sommerengineering.baraudio.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.baraudio.R

@Composable
fun LoginButton(
    imageId: Int,
    imagePadding: Dp,
    text: String,
    onClick: () -> Unit) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {

            Image(
                modifier = Modifier
                    .size(64.dp)
                    .padding(imagePadding),
                painter = painterResource(imageId),
                contentDescription = null)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 24.dp)) {

                // maximize text size for available space
                var multiplier by remember { mutableStateOf(1f) }

                CompositionLocalProvider(
                    LocalTextStyle provides LocalTextStyle.current.copy(
                        fontSize = 20.sp)) {

                    Text(
                        text = text,
                        maxLines = 1,
                        style = LocalTextStyle.current.copy(
                            fontSize = LocalTextStyle.current.fontSize * multiplier),
                        onTextLayout = {
                            if (it.hasVisualOverflow) {
                                multiplier *= 0.99f
                            }
                        })
                }
            }
        }
    }
}