package com.sommerengineering.baraudio.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sommerengineering.baraudio.R

val monospacedFontFamily = FontFamily(
    Font(R.font.ubuntu_mono_regular, FontWeight.Normal),
    Font(R.font.ubuntu_mono_bold, FontWeight.Bold))

val timestampTextStyle = TextStyle(
    fontFamily = monospacedFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp)