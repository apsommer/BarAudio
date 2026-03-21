package com.sommerengineering.baraudio.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.uitls.loginButtonSize

@Composable
fun LoginButton(
    iconRes: Int,
    onClick: () -> Unit) {

    Box(Modifier
        .size(loginButtonSize)
        .clip(CircleShape)
        .clickable { onClick() }
        .border(
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
            shape = CircleShape)) {
        Image(
            modifier = Modifier.size(loginButtonSize),
            painter = painterResource(iconRes),
            contentDescription = null)
    }
}