package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RetroDarkColorScheme = darkColorScheme(
  primary = AmberAccent,
  onPrimary = OledBlack,
  primaryContainer = DarkCharcoalElevated,
  onPrimaryContainer = AmberAccent,
  secondary = SecondaryText,
  onSecondary = PrimaryText,
  background = OledBlack,
  onBackground = PrimaryText,
  surface = DarkCharcoal,
  onSurface = PrimaryText,
  surfaceVariant = DarkCharcoalElevated,
  onSurfaceVariant = SecondaryText,
  outline = CardBorder
)

@Composable
fun RetroFlipClockTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = RetroDarkColorScheme,
    typography = RetroFlipTypography,
    content = content
  )
}
