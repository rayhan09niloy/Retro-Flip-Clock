package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val BebasNeueFontFamily = FontFamily(
  Font(R.font.bebas_neue, FontWeight.Normal)
)

val AntonFontFamily = FontFamily(
  Font(R.font.anton, FontWeight.Normal)
)

val RobotoMonoFontFamily = FontFamily(
  Font(R.font.roboto_mono, FontWeight.Normal),
  Font(R.font.roboto_mono, FontWeight.Bold)
)

// Active energetic bold display typeface
val PrimaryDisplayFontFamily = BebasNeueFontFamily

val RetroFlipTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = PrimaryDisplayFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 110.sp,
    lineHeight = 110.sp,
    letterSpacing = 0.sp,
    color = PrimaryText
  ),
  displayMedium = TextStyle(
    fontFamily = PrimaryDisplayFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 72.sp,
    lineHeight = 76.sp,
    letterSpacing = 0.sp,
    color = PrimaryText
  ),
  displaySmall = TextStyle(
    fontFamily = PrimaryDisplayFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 44.sp,
    lineHeight = 48.sp,
    letterSpacing = 0.sp,
    color = PrimaryText
  ),
  headlineMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp,
    color = PrimaryText
  ),
  titleLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp,
    color = PrimaryText
  ),
  titleMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = 0.15.sp,
    color = PrimaryText
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.5.sp,
    color = PrimaryText
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.25.sp,
    color = SecondaryText
  ),
  labelLarge = TextStyle(
    fontFamily = PrimaryDisplayFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp,
    color = PrimaryText
  ),
  labelMedium = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.5.sp,
    color = SecondaryText
  ),
  labelSmall = TextStyle(
    fontFamily = PrimaryDisplayFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.5.sp,
    color = SecondaryText
  )
)
