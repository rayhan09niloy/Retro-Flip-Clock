package com.rayhan09niloy.clockapp.screens

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
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
      // LANDSCAPE MODE: Digits and controls on left, laps list top-aligned on right
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.Top
      ) {
        Column(
          modifier = Modifier
            .weight(1.05f)
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

        // Right side: Laps list aligned to the top (newest lap visible at top)
        Box(
          modifier = Modifier
            .weight(0.95f)
            .fillMaxHeight()
            .padding(top = 8.dp)
        ) {
          LapListContent(laps = laps)
        }
      }
    } else {
      // PORTRAIT MODE: Digits top, controls middle, laps bottom
      Column(
        modifier = Modifier.fillMaxSize(),
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
  val listState = rememberLazyListState()

  // Ensure newest recorded lap (index 0) is immediately pinned at the top row
  LaunchedEffect(laps.firstOrNull()?.lapNumber) {
    if (laps.isNotEmpty()) {
      listState.scrollToItem(0)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    // Untracked sentence-case column headers in MetaMuted
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
          .fillMaxWidth()
          .weight(1f)
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
      // Remember initial highest lap so only newly inserted laps trigger the fade-in animation
      val initialHighestLap = remember { laps.firstOrNull()?.lapNumber ?: 0 }

      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.Top
      ) {
        items(laps, key = { it.lapNumber }) { lap ->
          val isLatest = lap.lapNumber == laps.firstOrNull()?.lapNumber
          val isNewInsertion = lap.lapNumber > initialHighestLap

          LapRowItem(
            lap = lap,
            isLatest = isLatest,
            shouldFadeIn = isNewInsertion,
            modifier = Modifier.animateItem()
          )
        }
      }
    }
  }
}

@Composable
private fun LapRowItem(
  lap: StopwatchLap,
  isLatest: Boolean,
  shouldFadeIn: Boolean,
  modifier: Modifier = Modifier
) {
  // Subtle CSS-style cubic-bezier fade-in animation for newly inserted lap rows
  val alphaAnim = remember(lap.lapNumber) {
    Animatable(if (shouldFadeIn) 0f else 1f)
  }
  val offsetYAnim = remember(lap.lapNumber) {
    Animatable(if (shouldFadeIn) -10f else 0f)
  }

  LaunchedEffect(lap.lapNumber) {
    if (shouldFadeIn) {
      coroutineScope {
        launch {
          alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
              durationMillis = 350,
              easing = FastOutSlowInEasing
            )
          )
        }
        launch {
          offsetYAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(
              durationMillis = 300,
              easing = FastOutSlowInEasing
            )
          )
        }
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer {
        alpha = alphaAnim.value
        translationY = offsetYAnim.value
      }
  ) {
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
        color = if (isLatest) SignalAmber else MetaMuted,
        fontSize = 13.sp
      )
      Text(
        text = formatLapMillis(lap.lapTimeMillis),
        style = FlipDigitStyle.copy(fontSize = 14.sp),
        color = if (isLatest) SignalAmber else FlapBone
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

private fun formatLapMillis(millis: Long): String {
  val minutes = (millis / 60000)
  val seconds = (millis % 60000) / 1000
  val hundredths = (millis % 1000) / 10
  return String.format(Locale.US, "%02d:%02d.%02d", minutes, seconds, hundredths)
}
