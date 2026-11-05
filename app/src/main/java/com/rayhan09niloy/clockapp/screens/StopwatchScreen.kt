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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.model.StopwatchLap
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.FlipDigitStyle
import com.rayhan09niloy.clockapp.ui.theme.FlapBone
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaLabelStyle
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel
import java.util.Locale

@Composable
fun StopwatchScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val elapsedMillis by viewModel.stopwatchMillis.collectAsState()
  val isRunning by viewModel.isStopwatchRunning.collectAsState()
  val laps by viewModel.stopwatchLaps.collectAsState()

  val minutes = (elapsedMillis / 60000)
  val seconds = (elapsedMillis % 60000) / 1000
  val hundredths = (elapsedMillis % 1000) / 10

  val mainDisplay = String.format(Locale.US, "%02d:%02d", minutes, seconds)
  val hundredthsDisplay = String.format(Locale.US, ".%02d", hundredths)

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    if (isLandscape) {
      // LANDSCAPE MODE: Digits and controls on left, laps list on right
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier
            .weight(1.1f)
            .fillMaxHeight(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = mainDisplay,
              style = FlipDigitStyle.copy(fontSize = 68.sp)
            )
            Text(
              text = hundredthsDisplay,
              style = FlipDigitStyle.copy(fontSize = 36.sp, color = SignalAmber),
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(24.dp))

          // Controls
          StopwatchControlsRow(
            elapsedMillis = elapsedMillis,
            isRunning = isRunning,
            onReset = { viewModel.resetStopwatch() },
            onToggle = { viewModel.toggleStopwatch() },
            onLap = { viewModel.recordLap() }
          )
        }

        // Right side: Laps list separated by 1.dp hairline dividers
        Box(
          modifier = Modifier
            .weight(0.9f)
            .fillMaxHeight()
        ) {
          LapListContent(laps = laps)
        }
      }
    } else {
      // PORTRAIT MODE: Digits top, controls middle, laps bottom
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Stopwatch",
          color = FlapBone,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.align(Alignment.Start)
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.85f),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = mainDisplay,
              style = FlipDigitStyle.copy(fontSize = 82.sp)
            )
            Text(
              text = hundredthsDisplay,
              style = FlipDigitStyle.copy(fontSize = 42.sp, color = SignalAmber),
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }
        }

        StopwatchControlsRow(
          elapsedMillis = elapsedMillis,
          isRunning = isRunning,
          onReset = { viewModel.resetStopwatch() },
          onToggle = { viewModel.toggleStopwatch() },
          onLap = { viewModel.recordLap() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Laps List directly on CanvasBlack
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1.15f)
        ) {
          LapListContent(laps = laps)
        }
      }
    }
  }
}

@Composable
private fun StopwatchControlsRow(
  elapsedMillis: Long,
  isRunning: Boolean,
  onReset: () -> Unit,
  onToggle: () -> Unit,
  onLap: () -> Unit
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Reset
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .border(1.dp, HairlineDivider, CircleShape)
        .clickable(
          enabled = elapsedMillis > 0,
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true, color = SignalAmber)
        ) { onReset() }
        .testTag("stopwatch_reset_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Refresh,
        contentDescription = "Reset",
        tint = if (elapsedMillis > 0) MetaMuted else HairlineDivider,
        modifier = Modifier.size(22.dp)
      )
    }

    Spacer(modifier = Modifier.width(28.dp))

    // Play/Pause
    Box(
      modifier = Modifier
        .size(68.dp)
        .clip(CircleShape)
        .background(if (isRunning) SignalAmber else FlapBone)
        .clickable(
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true, color = CanvasBlack)
        ) { onToggle() }
        .testTag("stopwatch_play_pause_btn"),
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

    // Lap
    Box(
      modifier = Modifier
        .size(52.dp)
        .clip(CircleShape)
        .border(1.dp, HairlineDivider, CircleShape)
        .clickable(
          enabled = isRunning,
          interactionSource = remember { MutableInteractionSource() },
          indication = ripple(bounded = true, color = SignalAmber)
        ) { onLap() }
        .testTag("stopwatch_lap_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Flag,
        contentDescription = "Lap",
        tint = if (isRunning) MetaMuted else HairlineDivider,
        modifier = Modifier.size(22.dp)
      )
    }
  }
}

@Composable
private fun LapListContent(
  laps: List<StopwatchLap>
) {
  Column(modifier = Modifier.fillMaxSize()) {
    // Untracked sentence-case column headers in MetaMuted (Floor #9)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "Lap", style = MetaLabelStyle, color = MetaMuted, fontSize = 12.sp)
      Text(text = "Lap time", style = MetaLabelStyle, color = MetaMuted, fontSize = 12.sp)
      Text(text = "Total", style = MetaLabelStyle, color = MetaMuted, fontSize = 12.sp)
    }

    // 1.dp hairline header divider
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(HairlineDivider)
    )

    if (laps.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Tap flag button to record laps",
          style = MetaLabelStyle,
          color = MetaMuted,
          fontSize = 13.sp
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize()
      ) {
        items(laps, key = { it.lapNumber }) { lap ->
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = String.format(Locale.US, "#%02d", lap.lapNumber),
                style = MetaLabelStyle,
                color = MetaMuted,
                fontSize = 13.sp
              )
              Text(
                text = formatLapMillis(lap.lapTimeMillis),
                style = FlipDigitStyle.copy(fontSize = 14.sp),
                color = FlapBone
              )
              Text(
                text = formatLapMillis(lap.splitTimeMillis),
                style = FlipDigitStyle.copy(fontSize = 14.sp),
                color = MetaMuted
              )
            }

            // 1.dp row divider directly on CanvasBlack
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HairlineDivider)
            )
          }
        }
      }
    }
  }
}

private fun formatLapMillis(millis: Long): String {
  val m = millis / 60000
  val s = (millis % 60000) / 1000
  val h = (millis % 1000) / 10
  return String.format(Locale.US, "%02d:%02d.%02d", m, s, h)
}
