/* Hallmark v2.0.0 | theme: custom-austere-frameless | genre: atmospheric | scope: app */
package com.rayhan09niloy.clockapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = SignalAmber,
  onPrimary = CanvasBlack,
  background = CanvasBlack,
  onBackground = FlapBone,
  surface = CanvasBlack,
  onSurface = FlapBone,
  outline = HairlineDivider
)

@Composable
fun RetroFlipClockTheme(
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = CanvasBlack.toArgb()
      window.navigationBarColor = CanvasBlack.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
    }
  }

  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = AppTypography,
    content = content
  )
}
