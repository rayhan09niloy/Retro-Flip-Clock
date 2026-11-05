package com.rayhan09niloy.clockapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.R

// Super bold display-type font family for numbers (weight 800 to 900)
val RussoOneDisplayFontFamily = FontFamily(
  Font(R.font.russo_one, FontWeight.Normal),
  Font(R.font.russo_one, FontWeight.Bold),
  Font(R.font.russo_one, FontWeight.ExtraBold),
  Font(R.font.russo_one, FontWeight.Black)
)

// Numeral font family configured specifically for displaying numbers only
val FlipNumeralFontFamily = RussoOneDisplayFontFamily

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
  fontSize = 32.sp,
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
