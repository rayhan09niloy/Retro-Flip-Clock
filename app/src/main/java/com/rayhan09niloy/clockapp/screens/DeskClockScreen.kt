package com.rayhan09niloy.clockapp.screens

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.model.AmbientTheme
import com.rayhan09niloy.clockapp.ui.theme.FlapBone
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaLabelStyle
import com.rayhan09niloy.clockapp.ui.theme.RunningSecondsStyle
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel

@Composable
fun DeskClockScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val hours by viewModel.currentHoursInt.collectAsState()
  val minutes by viewModel.currentMinutesInt.collectAsState()
  val secondsFormatted by viewModel.currentSecondsFormatted.collectAsState()
  val amPm by viewModel.currentAmPm.collectAsState()
  val weekday by viewModel.currentWeekdayFormatted.collectAsState()
  val dateFormatted by viewModel.currentDateFormatted.collectAsState()
  val settings by viewModel.settings.collectAsState()

  val currentTheme = AmbientTheme.fromName(settings.ambientTheme)
  val animatedBackgroundColor by animateColorAsState(
    targetValue = currentTheme.backgroundColor,
    animationSpec = tween(durationMillis = 350),
    label = "ambient_bg_color"
  )

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(animatedBackgroundColor)
      .statusBarsPadding()
      .padding(
        horizontal = if (isLandscape) 12.dp else 16.dp,
        vertical = if (isLandscape) 2.dp else 8.dp
      )
  ) {
    if (isLandscape) {
      // LANDSCAPE MODE: Digits expand to take up 100% of available screen space
      val availableHeight = (maxHeight - 36.dp).coerceAtLeast(120.dp)
      val availableWidth = ((maxWidth - 16.dp) / 2).coerceAtLeast(160.dp)

      val fontSzFromHeight = availableHeight.value * 0.98f
      val fontSzFromWidth = (availableWidth.value / 1.18f)
      val fontSz = minOf(fontSzFromHeight, fontSzFromWidth).sp

      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Header Row in Landscape: Date, Ambient Theme Cycler Pill, Weekday
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = dateFormatted,
            style = MetaLabelStyle
          )

          // Subtle ambient background theme cycler pill
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(currentTheme.pillSurfaceColor)
              .border(0.5.dp, HairlineDivider, RoundedCornerShape(14.dp))
              .clickable { viewModel.cycleAmbientTheme() }
              .padding(horizontal = 10.dp, vertical = 4.dp)
              .testTag("theme_cycler_btn_landscape"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(currentTheme.accentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = currentTheme.displayName,
              color = FlapBone,
              fontSize = 11.sp,
              style = MetaLabelStyle
            )
          }

          Text(
            text = weekday,
            style = MetaLabelStyle
          )
        }

        // Center Area: Hours on Left, Minutes on Right side-by-side
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left Column: Hours + AM/PM
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            FlipCard(
              value = hours,
              cardWidth = availableWidth,
              cardHeight = availableHeight,
              fontSize = fontSz,
              backgroundColor = animatedBackgroundColor,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )

            Box(
              modifier = Modifier
                .width(availableWidth)
                .padding(top = 2.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              Text(
                text = amPm,
                style = MetaLabelStyle
              )
            }
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Right Column: Minutes + Running Seconds
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            FlipCard(
              value = minutes,
              cardWidth = availableWidth,
              cardHeight = availableHeight,
              fontSize = fontSz,
              backgroundColor = animatedBackgroundColor,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )

            Box(
              modifier = Modifier
                .width(availableWidth)
                .padding(top = 2.dp),
              contentAlignment = Alignment.CenterEnd
            ) {
              Text(
                text = secondsFormatted,
                style = RunningSecondsStyle
              )
            }
          }
        }
      }
    } else {
      // PORTRAIT MODE: Frameless digit groups stacked vertically filling full space
      val cardW = (maxWidth - 16.dp).coerceAtLeast(260.dp)
      val cardH = ((maxHeight - 90.dp) / 2).coerceAtLeast(160.dp)
      val fontSz = minOf(cardH.value * 0.96f, cardW.value / 1.15f).sp

      Column(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Header Row: Date on Left, Subtle Ambient Theme Cycler Pill on Right
        Row(
          modifier = Modifier
            .width(cardW)
            .padding(bottom = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = dateFormatted,
            style = MetaLabelStyle
          )

          // Subtle ambient background theme cycler badge
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(currentTheme.pillSurfaceColor)
              .border(0.5.dp, HairlineDivider, RoundedCornerShape(14.dp))
              .clickable { viewModel.cycleAmbientTheme() }
              .padding(horizontal = 9.dp, vertical = 4.dp)
              .testTag("theme_cycler_btn"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(currentTheme.accentColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = currentTheme.displayName,
              color = FlapBone,
              fontSize = 11.sp,
              style = MetaLabelStyle
            )
          }
        }

        // Upper Frameless Digits: Hours
        FlipCard(
          value = hours,
          cardWidth = cardW,
          cardHeight = cardH,
          fontSize = fontSz,
          backgroundColor = animatedBackgroundColor,
          onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
        )

        // Below Hours: Bottom-left AM/PM indicator
        Box(
          modifier = Modifier
            .width(cardW)
            .padding(top = 4.dp, bottom = 10.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = amPm,
            style = MetaLabelStyle
          )
        }

        // Above Minutes: Centered Day of Week
        Text(
          text = weekday,
          style = MetaLabelStyle,
          modifier = Modifier.padding(bottom = 6.dp)
        )

        // Lower Frameless Digits: Minutes
        FlipCard(
          value = minutes,
          cardWidth = cardW,
          cardHeight = cardH,
          fontSize = fontSz,
          backgroundColor = animatedBackgroundColor,
          onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
        )

        // Below Minutes: Bottom-right running 2-digit seconds counter
        Box(
          modifier = Modifier
            .width(cardW)
            .padding(top = 4.dp),
          contentAlignment = Alignment.CenterEnd
        ) {
          Text(
            text = secondsFormatted,
            style = RunningSecondsStyle
          )
        }
      }
    }
  }
}
