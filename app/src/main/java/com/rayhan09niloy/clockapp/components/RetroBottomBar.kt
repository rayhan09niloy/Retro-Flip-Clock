package com.rayhan09niloy.clockapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber

enum class ClockTab(val icon: ImageVector, val contentDesc: String) {
  CLOCK(Icons.Default.AccessTime, "Clock"),
  WORLD(Icons.Default.Public, "World"),
  STOPWATCH(Icons.Default.Timer, "Stopwatch"),
  TIMER(Icons.Default.HourglassBottom, "Timer"),
  ALARM(Icons.Default.Alarm, "Alarm")
}

@Composable
fun RetroBottomBar(
  currentTab: ClockTab,
  onTabSelected: (ClockTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(CanvasBlack)
      .navigationBarsPadding()
  ) {
    // 1.dp hairline top divider
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .align(Alignment.TopCenter)
        .background(HairlineDivider)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ClockTab.values().forEach { tab ->
        val isSelected = currentTab == tab
        val interactionSource = remember { MutableInteractionSource() }

        Box(
          modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clickable(
              interactionSource = interactionSource,
              indication = ripple(bounded = false, radius = 24.dp, color = SignalAmber)
            ) { onTabSelected(tab) }
            .testTag("nav_tab_${tab.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.contentDesc,
            tint = if (isSelected) SignalAmber else MetaMuted,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
