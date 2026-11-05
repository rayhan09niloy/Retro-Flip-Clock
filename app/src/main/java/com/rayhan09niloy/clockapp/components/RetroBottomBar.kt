package com.rayhan09niloy.clockapp.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.ui.theme.AmberAccent
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.SecondaryText

enum class ClockTab(val title: String, val icon: ImageVector) {
  CLOCK("Clock", Icons.Default.AccessTime),
  WORLD("World", Icons.Default.Public),
  STOPWATCH("Stopwatch", Icons.Default.Timer),
  TIMER("Timer", Icons.Default.HourglassBottom),
  ALARM("Alarm", Icons.Default.Alarm)
}

@Composable
fun RetroBottomBar(
  currentTab: ClockTab,
  onTabSelected: (ClockTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
  val barHeight = if (isLandscape) 48.dp else 62.dp
  val itemHeight = if (isLandscape) 42.dp else 54.dp
  val iconSize = if (isLandscape) 18.dp else 22.dp

  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(DarkCharcoal)
      .border(0.5.dp, CardBorder)
      .navigationBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(barHeight)
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      ClockTab.values().forEach { tab ->
        val isSelected = currentTab == tab
        val interactionSource = remember { MutableInteractionSource() }

        Column(
          modifier = Modifier
            .weight(1f)
            .height(itemHeight)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
              interactionSource = interactionSource,
              indication = ripple(bounded = true, color = AmberAccent)
            ) { onTabSelected(tab) }
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(width = if (isLandscape) 36.dp else 44.dp, height = if (isLandscape) 22.dp else 26.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) AmberAccent.copy(alpha = 0.18f) else DarkCharcoal),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = tab.icon,
              contentDescription = tab.title,
              tint = if (isSelected) AmberAccent else SecondaryText,
              modifier = Modifier.size(iconSize)
            )
          }
          if (!isLandscape) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = tab.title,
              color = if (isSelected) AmberAccent else SecondaryText,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }
    }
  }
}
