package com.rayhan09niloy.clockapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
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

  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    if (isLandscape) {
      // LANDSCAPE MODE: Hours on Left, Minutes on Right side-by-side
      val cardW = ((maxWidth - 64.dp) / 2).coerceIn(180.dp, 380.dp)
      val cardH = (maxHeight - 48.dp).coerceIn(140.dp, 300.dp)
      val fontSz = (cardH.value * 0.94f).sp

      Row(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.Center),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: Date above, Hours, AM/PM below
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = dateFormatted,
            style = MetaLabelStyle,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          FlipCard(
            value = hours,
            cardWidth = cardW,
            cardHeight = cardH,
            fontSize = fontSz,
            onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
          )

          Box(
            modifier = Modifier
              .width(cardW)
              .padding(top = 4.dp),
            contentAlignment = Alignment.CenterStart
          ) {
            Text(
              text = amPm,
              style = MetaLabelStyle
            )
          }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right Column: Weekday above, Minutes, Running Seconds below-right
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Text(
            text = weekday,
            style = MetaLabelStyle,
            modifier = Modifier.padding(bottom = 6.dp)
          )

          FlipCard(
            value = minutes,
            cardWidth = cardW,
            cardHeight = cardH,
            fontSize = fontSz,
            onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
          )

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
    } else {
      // PORTRAIT MODE: Exactly two frameless digit groups stacked vertically
      // Hours on top, Minutes on bottom.
      val cardW = (maxWidth - 32.dp).coerceIn(240.dp, 380.dp)
      val cardH = ((maxHeight - 140.dp) / 2).coerceIn(140.dp, 240.dp)
      val fontSz = (cardH.value * 0.94f).sp

      Column(
        modifier = Modifier
          .fillMaxSize()
          .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Above Hours: Centered Date in MM/dd/yy (11/05/26) format
        Text(
          text = dateFormatted,
          style = MetaLabelStyle,
          modifier = Modifier.padding(bottom = 6.dp)
        )

        // Upper Frameless Digits: Hours
        FlipCard(
          value = hours,
          cardWidth = cardW,
          cardHeight = cardH,
          fontSize = fontSz,
          onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
        )

        // Below Hours: Bottom-left AM/PM indicator
        Box(
          modifier = Modifier
            .width(cardW)
            .padding(top = 4.dp, bottom = 12.dp),
          contentAlignment = Alignment.CenterStart
        ) {
          Text(
            text = amPm,
            style = MetaLabelStyle
          )
        }

        // Above Minutes: Centered Day of Week (Thursday, sentence case)
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
          onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
        )

        // Below Minutes: Bottom-right running 2-digit seconds counter (28.sp FlapBone)
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
