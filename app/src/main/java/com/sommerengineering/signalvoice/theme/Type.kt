package com.sommerengineering.signalvoice.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sommerengineering.signalvoice.R

val fontFamily = FontFamily(
    Font(R.font.ubuntu_light, FontWeight.Light),
    Font(R.font.ubuntu_regular, FontWeight.Normal),
    Font(R.font.ubuntu_medium, FontWeight.Medium),
    Font(R.font.ubuntu_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = fontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = fontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = Typography().headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = fontFamily),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = fontFamily),
    titleLarge = Typography().titleLarge.copy(fontFamily = fontFamily),
    titleMedium = Typography().titleMedium.copy(fontFamily = fontFamily),
    titleSmall = Typography().titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = Typography().bodyLarge.copy(fontFamily = fontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = fontFamily),
    labelLarge = Typography().labelLarge.copy(fontFamily = fontFamily),
    labelMedium = Typography().labelMedium.copy(fontFamily = fontFamily),
    labelSmall = Typography().labelSmall.copy(fontFamily = fontFamily)
)

// timestamp monospace
val monospacedFontFamily = FontFamily(
    Font(R.font.ubuntu_mono_regular, FontWeight.Normal),
    Font(R.font.ubuntu_mono_bold, FontWeight.Bold)
)

val timestampTextStyle = TextStyle(
    fontFamily = monospacedFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp
)