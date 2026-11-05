package com.rayhan09niloy.clockapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

// Heavy, wide geometric sans-serif with strictly tabular numerals
val FlipNumeralFontFamily = FontFamily.SansSerif

val FlipDigitStyle = TextStyle(
  fontFamily = FlipNumeralFontFamily,
  fontWeight = FontWeight.Black,
  color = FlapBone,
  fontFeatureSettings = "tnum",
  platformStyle = PlatformTextStyle(includeFontPadding = false),
  lineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
  )
)

val RunningSecondsStyle = TextStyle(
  fontFamily = FlipNumeralFontFamily,
  fontWeight = FontWeight.Black,
  fontSize = 28.sp,
  color = FlapBone,
  fontFeatureSettings = "tnum",
  platformStyle = PlatformTextStyle(includeFontPadding = false),
  lineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
  )
)

val MetaLabelStyle = TextStyle(
  fontFamily = FontFamily.SansSerif,
  fontWeight = FontWeight.Normal,
  fontSize = 14.sp,
  color = MetaMuted,
  platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val AppTypography = Typography(
  bodyLarge = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = FlapBone
  )
)
