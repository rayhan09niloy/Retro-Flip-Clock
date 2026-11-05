package com.rayhan09niloy.clockapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.model.StopwatchLap
import com.rayhan09niloy.clockapp.ui.theme.AmberAccent
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoalElevated
import com.rayhan09niloy.clockapp.ui.theme.GreenAccent
import com.rayhan09niloy.clockapp.ui.theme.OledBlack
import com.rayhan09niloy.clockapp.ui.theme.PrimaryText
import com.rayhan09niloy.clockapp.ui.theme.RedAccent
import com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
import com.rayhan09niloy.clockapp.ui.theme.SecondaryText
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

  // Format main time
  val minutes = (elapsedMillis / 60000)
  val seconds = (elapsedMillis % 60000) / 1000
  val hundredths = (elapsedMillis % 1000) / 10

  val mainDisplay = String.format(Locale.US, "%02d:%02d", minutes, seconds)
  val hundredthsDisplay = String.format(Locale.US, ".%02d", hundredths)

  val settings by viewModel.settings.collectAsState()
  val currentFontFamily = when (settings.fontStyle) {
    "Anton" -> com.rayhan09niloy.clockapp.ui.theme.AntonFontFamily
    "Roboto Mono" -> com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
    else -> com.rayhan09niloy.clockapp.ui.theme.BebasNeueFontFamily
  }

  val minLapTime = remember(laps) {
    if (laps.size >= 2) laps.minOfOrNull { it.lapTimeMillis } else null
  }
  val maxLapTime = remember(laps) {
    if (laps.size >= 2) laps.maxOfOrNull { it.lapTimeMillis } else null
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(OledBlack)
      .statusBarsPadding()
      .padding(horizontal = 16.dp)
  ) {
    val isLandscape = maxWidth > maxHeight

    if (isLandscape) {
      // LANDSCAPE: SIDE BY SIDE (Left: Giant Timer & Controls, Right: Laps Table)
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 60.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: Digits + Controls
        Column(
          modifier = Modifier
            .weight(1.1f)
            .fillMaxHeight(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceEvenly
        ) {
          Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = mainDisplay,
              color = PrimaryText,
              fontSize = 72.sp,
              fontFamily = currentFontFamily,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = hundredthsDisplay,
              color = AmberAccent,
              fontSize = 38.sp,
              fontFamily = currentFontFamily,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }

          // Controls Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { viewModel.resetStopwatch() },
              enabled = elapsedMillis > 0,
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkCharcoal)
                .border(1.dp, CardBorder, CircleShape)
                .testTag("stopwatch_reset_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = if (elapsedMillis > 0) PrimaryText else SecondaryText.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
              )
            }

            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (isRunning) AmberAccent else Color(0xFFEDEDED))
                .clickable { viewModel.toggleStopwatch() }
                .testTag("stopwatch_play_pause_btn"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start",
                tint = OledBlack,
                modifier = Modifier.size(38.dp)
              )
            }

            IconButton(
              onClick = { viewModel.recordLap() },
              enabled = isRunning,
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DarkCharcoal)
                .border(1.dp, CardBorder, CircleShape)
                .testTag("stopwatch_lap_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = "Lap",
                tint = if (isRunning) AmberAccent else SecondaryText.copy(alpha = 0.4f),
                modifier = Modifier.size(22.dp)
              )
            }
          }
        }

        // Right Column: Laps Table
        Card(
          modifier = Modifier
            .weight(0.9f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
        ) {
          LapListContent(
            laps = laps,
            minLapTime = minLapTime,
            maxLapTime = maxLapTime
          )
        }
      }
    } else {
      // PORTRAIT LAYOUT
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Stopwatch",
          color = PrimaryText,
          fontSize = 28.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.align(Alignment.Start)
        )

        // Center Screen Digits
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.9f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.Center
            ) {
              Text(
                text = mainDisplay,
                color = PrimaryText,
                fontSize = 86.sp,
                fontFamily = currentFontFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
              Text(
                text = hundredthsDisplay,
                color = AmberAccent,
                fontSize = 44.sp,
                fontFamily = currentFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
              )
            }

            if (laps.isNotEmpty()) {
              val currentLapElapsed = laps.firstOrNull()?.let {
                elapsedMillis - it.splitTimeMillis
              } ?: elapsedMillis
              val lapM = currentLapElapsed / 60000
              val lapS = (currentLapElapsed % 60000) / 1000
              val lapH = (currentLapElapsed % 1000) / 10
              Text(
                text = String.format(Locale.US, "Lap %d  %02d:%02d.%02d", laps.size + 1, lapM, lapS, lapH),
                color = SecondaryText,
                fontSize = 15.sp,
                fontFamily = RobotoMonoFontFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
              )
            }
          }
        }

        // Controls Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { viewModel.resetStopwatch() },
            enabled = elapsedMillis > 0,
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(DarkCharcoal)
              .border(1.dp, CardBorder, CircleShape)
              .testTag("stopwatch_reset_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reset",
              tint = if (elapsedMillis > 0) PrimaryText else SecondaryText.copy(alpha = 0.4f),
              modifier = Modifier.size(24.dp)
            )
          }

          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(if (isRunning) AmberAccent else Color(0xFFEDEDED))
              .clickable { viewModel.toggleStopwatch() }
              .testTag("stopwatch_play_pause_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isRunning) "Pause" else "Start",
              tint = OledBlack,
              modifier = Modifier.size(42.dp)
            )
          }

          IconButton(
            onClick = { viewModel.recordLap() },
            enabled = isRunning,
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(DarkCharcoal)
              .border(1.dp, CardBorder, CircleShape)
              .testTag("stopwatch_lap_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Flag,
              contentDescription = "Lap",
              tint = if (isRunning) AmberAccent else SecondaryText.copy(alpha = 0.4f),
              modifier = Modifier.size(24.dp)
            )
          }
        }

        // Bottom Section: Lap Times
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1.1f)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
        ) {
          LapListContent(
            laps = laps,
            minLapTime = minLapTime,
            maxLapTime = maxLapTime
          )
        }
      }
    }
  }
}

@Composable
private fun LapListContent(
  laps: List<StopwatchLap>,
  minLapTime: Long?,
  maxLapTime: Long?
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp, vertical = 10.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "LAP", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text(text = "LAP TIME", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      Text(text = "TOTAL", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    if (laps.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "Tap the Flag button to record laps",
          color = SecondaryText.copy(alpha = 0.7f),
          fontSize = 12.sp
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        items(laps, key = { it.lapNumber }) { lap ->
          val isFastest = minLapTime != null && lap.lapTimeMillis == minLapTime
          val isSlowest = maxLapTime != null && lap.lapTimeMillis == maxLapTime && minLapTime != maxLapTime

          val lapColor = when {
            isFastest -> GreenAccent
            isSlowest -> RedAccent
            else -> PrimaryText
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(DarkCharcoalElevated)
              .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = String.format(Locale.US, "#%02d", lap.lapNumber),
              color = SecondaryText,
              fontSize = 12.sp,
              fontFamily = RobotoMonoFontFamily,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = formatLapMillis(lap.lapTimeMillis),
              color = lapColor,
              fontSize = 13.sp,
              fontFamily = RobotoMonoFontFamily,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = formatLapMillis(lap.splitTimeMillis),
              color = PrimaryText,
              fontSize = 13.sp,
              fontFamily = RobotoMonoFontFamily,
              fontWeight = FontWeight.Medium
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
