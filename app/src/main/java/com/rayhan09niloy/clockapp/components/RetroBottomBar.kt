package com.rayhan09niloy.clockapp.components

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.R
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber

enum class ClockTab(
  @DrawableRes val iconRes: Int,
  val label: String
) {
  CLOCK(R.drawable.ic_nav_home, "Clock"),
  ALARM(R.drawable.ic_nav_alarm, "Alarm"),
  TIMER(R.drawable.ic_nav_timer, "Timer"),
  STOPWATCH(R.drawable.ic_nav_stopwatch, "Stopwatch"),
  WORLD(R.drawable.ic_nav_world, "World")
}

@Composable
fun RetroBottomBar(
  currentTab: ClockTab,
  onTabSelected: (ClockTab) -> Unit,
  modifier: Modifier = Modifier,
  isLandscape: Boolean = false
) {
  if (isLandscape) {
    // Landscape Mode: Floating vertical navigation pill on the right side
    // 16px (16.dp) margin from right, top, and bottom
    Box(
      modifier = modifier
        .navigationBarsPadding()
        .padding(end = 16.dp, top = 16.dp, bottom = 16.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .width(70.dp)
          .wrapContentHeight()
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFF141416))
          .border(1.dp, HairlineDivider, RoundedCornerShape(26.dp))
          .padding(vertical = 8.dp, horizontal = 4.dp)
      ) {
        Column(
          modifier = Modifier.width(62.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          ClockTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val interactionSource = remember { MutableInteractionSource() }

            Column(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) SignalAmber.copy(alpha = 0.14f) else Color.Transparent)
                .clickable(
                  interactionSource = interactionSource,
                  indication = ripple(bounded = true, color = SignalAmber)
                ) { onTabSelected(tab) }
                .padding(vertical = 6.dp)
                .testTag("nav_tab_${tab.name.lowercase()}"),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                painter = painterResource(id = tab.iconRes),
                contentDescription = tab.label,
                tint = if (isSelected) SignalAmber else MetaMuted,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = tab.label,
                color = if (isSelected) SignalAmber else MetaMuted,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  } else {
    // Portrait Mode: Floating horizontal navigation pill at the bottom
    // 16px (16.dp) margin from left, right, and bottom
    Box(
      modifier = modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(26.dp))
          .background(Color(0xFF141416))
          .border(1.dp, HairlineDivider, RoundedCornerShape(26.dp))
          .padding(horizontal = 6.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          ClockTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            val interactionSource = remember { MutableInteractionSource() }

            Column(
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isSelected) SignalAmber.copy(alpha = 0.14f) else Color.Transparent)
                .clickable(
                  interactionSource = interactionSource,
                  indication = ripple(bounded = true, color = SignalAmber)
                ) { onTabSelected(tab) }
                .padding(vertical = 6.dp)
                .testTag("nav_tab_${tab.name.lowercase()}"),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                painter = painterResource(id = tab.iconRes),
                contentDescription = tab.label,
                tint = if (isSelected) SignalAmber else MetaMuted,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(3.dp))
              Text(
                text = tab.label,
                color = if (isSelected) SignalAmber else MetaMuted,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}
