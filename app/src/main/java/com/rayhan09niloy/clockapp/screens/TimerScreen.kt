package com.rayhan09niloy.clockapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.FlapBone
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaLabelStyle
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel

@Composable
fun TimerScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val activityTag by viewModel.timerActivityTag.collectAsState()
  val remainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
  val isRunning by viewModel.isTimerRunning.collectAsState()

  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    val cardW = if (isLandscape) {
      ((maxWidth - 24.dp) / 2).coerceAtLeast(160.dp)
    } else {
      (maxWidth - 16.dp).coerceAtLeast(240.dp)
    }

    val cardH = if (isLandscape) {
      (maxHeight - 80.dp).coerceAtLeast(120.dp)
    } else {
      ((maxHeight - 160.dp) / 2).coerceAtLeast(140.dp)
    }

    val fontSz = if (isLandscape) {
      minOf(cardH.value * 0.96f, cardW.value / 1.18f).sp
    } else {
      minOf(cardH.value * 0.96f, cardW.value / 1.15f).sp
    }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Center Tag: Simple pill without decorative dot or glued arrow (Floor #10)
      Box(
        modifier = Modifier
          .padding(top = 10.dp)
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, HairlineDivider, RoundedCornerShape(16.dp))
          .background(CanvasBlack)
          .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = activityTag,
          color = FlapBone,
          fontSize = 13.sp,
          style = MetaLabelStyle
        )
      }

      // Middle Area: Stacked Minutes on Top, Seconds on Bottom (Portrait) or Side by Side (Landscape)
      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
      ) {
        if (isLandscape) {
          Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Minutes",
                style = MetaLabelStyle,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              FlipCard(
                value = minutes,
                cardWidth = cardW,
                cardHeight = cardH,
                fontSize = fontSz,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "Seconds",
                style = MetaLabelStyle,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              FlipCard(
                value = seconds,
                cardWidth = cardW,
                cardHeight = cardH,
                fontSize = fontSz,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }
          }
        } else {
          // Portrait: Stack Minutes on top, Seconds on bottom
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            FlipCard(
              value = minutes,
              cardWidth = cardW,
              cardHeight = cardH,
              fontSize = fontSz,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            FlipCard(
              value = seconds,
              cardWidth = cardW,
              cardHeight = cardH,
              fontSize = fontSz,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )
          }
        }
      }

      // Bottom Controls: Three clean circular icon buttons (Reset, Play/Pause, Skip) with zero card background
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reset Button
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .border(1.dp, HairlineDivider, CircleShape)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = true, color = SignalAmber)
            ) { viewModel.resetTimer() }
            .testTag("timer_reset_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset",
            tint = MetaMuted,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(28.dp))

        // Play/Pause Button
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(if (isRunning) SignalAmber else FlapBone)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = true, color = CanvasBlack)
            ) {
              if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
            }
            .testTag("timer_play_pause_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isRunning) "Pause" else "Start",
            tint = CanvasBlack,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.width(28.dp))

        // Skip / Fast-forward Button
        Box(
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .border(1.dp, HairlineDivider, CircleShape)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = true, color = SignalAmber)
            ) {
              viewModel.setTimerDuration(0)
            }
            .testTag("timer_skip_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = "Skip",
            tint = MetaMuted,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
