package com.rayhan09niloy.clockapp.model

import androidx.compose.ui.graphics.Color

enum class AmbientTheme(
  val displayName: String,
  val backgroundColor: Color,
  val cardBackground: Color,
  val pillSurfaceColor: Color,
  val accentColor: Color
) {
  OLED_BLACK(
    displayName = "OLED Black",
    backgroundColor = Color(0xFF000000),
    cardBackground = Color(0xFF000000),
    pillSurfaceColor = Color(0xFF141416),
    accentColor = Color(0xFFFFB800)
  ),
  MIDNIGHT_BLUE(
    displayName = "Midnight Blue",
    backgroundColor = Color(0xFF090E17),
    cardBackground = Color(0xFF090E17),
    pillSurfaceColor = Color(0xFF131D2E),
    accentColor = Color(0xFF60A5FA)
  ),
  DEEP_CHARCOAL(
    displayName = "Deep Charcoal",
    backgroundColor = Color(0xFF141418),
    cardBackground = Color(0xFF141418),
    pillSurfaceColor = Color(0xFF202026),
    accentColor = Color(0xFFFFB800)
  ),
  SLATE_GRAY(
    displayName = "Slate Gray",
    backgroundColor = Color(0xFF101418),
    cardBackground = Color(0xFF101418),
    pillSurfaceColor = Color(0xFF181F26),
    accentColor = Color(0xFF94A3B8)
  );

  companion object {
    fun fromName(name: String): AmbientTheme {
      return values().find { it.displayName.equals(name, ignoreCase = true) } ?: OLED_BLACK
    }
  }

  fun next(): AmbientTheme {
    val all = values()
    return all[(ordinal + 1) % all.size]
  }
}
