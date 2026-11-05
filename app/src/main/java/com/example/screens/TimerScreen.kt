package com.example.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.FlipCard
import com.example.components.WheelPicker
import com.example.model.FocusSession
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.DarkCharcoalElevated
import com.example.ui.theme.OledBlack
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SecondaryText
import com.example.viewmodel.ClockViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val activityTag by viewModel.timerActivityTag.collectAsState()
  val remainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
  val totalSeconds by viewModel.timerTotalSeconds.collectAsState()
  val isRunning by viewModel.isTimerRunning.collectAsState()
  val isPaused by viewModel.isTimerPaused.collectAsState()
  val focusSessions by viewModel.focusSessions.collectAsState()
  val settings by viewModel.settings.collectAsState()

  var showTagPickerSheet by remember { mutableStateOf(false) }
  var showHistorySheet by remember { mutableStateOf(false) }

  val hours = remainingSeconds / 3600
  val minutes = (remainingSeconds % 3600) / 60
  val seconds = remainingSeconds % 60

  val minutesStr = String.format(Locale.US, "%02d", if (hours > 0) hours * 60 + minutes else minutes)
  val secondsStr = String.format(Locale.US, "%02d", seconds)

  var pickerHours by remember { mutableIntStateOf(totalSeconds / 3600) }
  var pickerMinutes by remember { mutableIntStateOf((totalSeconds % 3600) / 60) }
  var pickerSeconds by remember { mutableIntStateOf(totalSeconds % 60) }

  val currentFontFamily = when (settings.fontStyle) {
    "Anton" -> com.example.ui.theme.AntonFontFamily
    "Roboto Mono" -> com.example.ui.theme.RobotoMonoFontFamily
    else -> com.example.ui.theme.BebasNeueFontFamily
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(OledBlack)
      .statusBarsPadding()
      .padding(horizontal = 14.dp)
  ) {
    val isLandscape = maxWidth > maxHeight

    val cardWidth = if (isLandscape) {
      ((maxWidth - 72.dp) / 2).coerceIn(180.dp, 360.dp)
    } else {
      (maxWidth - 40.dp).coerceIn(240.dp, 400.dp)
    }

    val cardHeight = if (isLandscape) {
      (maxHeight - 145.dp).coerceIn(130.dp, 240.dp)
    } else {
      ((maxHeight - 230.dp) / 2).coerceIn(140.dp, 230.dp)
    }

    val fontSize = (cardHeight.value * 0.94f).sp

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = if (isLandscape) 64.dp else 76.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // --- Top Tag: Activity Pill Button ---
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = if (isLandscape) 4.dp else 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Tag Pill Button
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkCharcoalElevated)
            .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable { showTagPickerSheet = true }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("timer_activity_pill"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Tag,
            contentDescription = "Activity Tag",
            tint = AmberAccent,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = activityTag,
            color = AmberAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // History Chart Icon Button
        IconButton(
          onClick = { showHistorySheet = true },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(DarkCharcoal)
            .border(0.5.dp, CardBorder, CircleShape)
            .testTag("timer_history_btn")
        ) {
          Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = "Focus History",
            tint = AmberAccent,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        if (isLandscape) {
          // LANDSCAPE: SIDE BY SIDE (Minutes on Left, Seconds on Right)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "MINUTES",
                color = SecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 2.dp)
              )
              FlipCard(
                value = minutesStr,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "SECONDS",
                color = SecondaryText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 2.dp)
              )
              FlipCard(
                value = secondsStr,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }
          }
        } else {
          // PORTRAIT: STACKED (Minutes on Top, Seconds on Bottom)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "MINUTES",
                color = SecondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 3.dp)
              )
              FlipCard(
                value = minutesStr,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "SECONDS",
                color = SecondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 3.dp)
              )
              FlipCard(
                value = secondsStr,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }
          }
        }
      }

      // --- Bottom Section: Presets & Controls ---
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Quick Presets Row
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val presets = listOf(
            60 to "1 min",
            300 to "5 min",
            900 to "15 min",
            1500 to "25 min (Pomodoro)",
            2700 to "45 min",
            3600 to "60 min"
          )
          items(presets) { (sec, label) ->
            val isCurrent = totalSeconds == sec
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isCurrent) AmberAccent.copy(alpha = 0.2f) else DarkCharcoal)
                .border(1.dp, if (isCurrent) AmberAccent else CardBorder, RoundedCornerShape(20.dp))
                .clickable {
                  viewModel.setTimerDuration(sec)
                }
                .padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = if (isCurrent) AmberAccent else SecondaryText,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = label,
                color = if (isCurrent) AmberAccent else PrimaryText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        // Main Controls: Round White Play/Pause & Reset
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Reset Button
          IconButton(
            onClick = { viewModel.resetTimer() },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(DarkCharcoal)
              .border(1.dp, CardBorder, CircleShape)
              .testTag("timer_reset_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = "Reset Timer",
              tint = PrimaryText,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(28.dp))

          // Large Round White Play/Pause Button
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(if (isRunning) AmberAccent else Color(0xFFEDEDED))
              .clickable {
                if (isRunning) viewModel.pauseTimer() else viewModel.startTimer()
              }
              .testTag("timer_play_pause_btn"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isRunning) "Pause" else "Start",
              tint = OledBlack,
              modifier = Modifier.size(42.dp)
            )
          }

          Spacer(modifier = Modifier.width(28.dp))

          // Custom Duration Picker Sheet Opener
          IconButton(
            onClick = {
              pickerHours = totalSeconds / 3600
              pickerMinutes = (totalSeconds % 3600) / 60
              pickerSeconds = totalSeconds % 60
              showTagPickerSheet = false
              // We can open a custom duration dialog
            },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(DarkCharcoal)
              .border(1.dp, CardBorder, CircleShape)
              .testTag("timer_adjust_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Timelapse,
              contentDescription = "Set Custom Time",
              tint = SecondaryText,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }

    // --- Activity Tag Picker Sheet ---
    if (showTagPickerSheet) {
      ModalBottomSheet(
        onDismissRequest = { showTagPickerSheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkCharcoal,
        scrimColor = Color.Black.copy(alpha = 0.7f)
      ) {
        ActivityTagSheetContent(
          currentTag = activityTag,
          onSelectTag = {
            viewModel.setTimerActivityTag(it)
            showTagPickerSheet = false
          },
          onDismiss = { showTagPickerSheet = false }
        )
      }
    }

    // --- Focus Time History Sheet ---
    if (showHistorySheet) {
      ModalBottomSheet(
        onDismissRequest = { showHistorySheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkCharcoal,
        scrimColor = Color.Black.copy(alpha = 0.7f)
      ) {
        FocusHistorySheetContent(
          sessions = focusSessions,
          onDismiss = { showHistorySheet = false }
        )
      }
    }
  }
}

@Composable
private fun ActivityTagSheetContent(
  currentTag: String,
  onSelectTag: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var customTag by remember { mutableStateOf("") }
  val predefined = listOf("Study", "Work", "Reading", "Deep Work", "Coding", "Writing", "Meditation")

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp)
  ) {
    Text(
      text = "Select Focus Activity",
      color = PrimaryText,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))

    predefined.chunked(3).forEach { rowTags ->
      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        rowTags.forEach { tag ->
          val isSelected = tag.equals(currentTag, ignoreCase = true)
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) AmberAccent else DarkCharcoalElevated)
              .border(1.dp, if (isSelected) AmberAccent else CardBorder, RoundedCornerShape(12.dp))
              .clickable { onSelectTag(tag) }
              .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = tag,
              color = if (isSelected) OledBlack else PrimaryText,
              fontWeight = FontWeight.SemiBold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Custom Tag input
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = customTag,
        onValueChange = { customTag = it },
        placeholder = { Text("Custom activity name") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = AmberAccent,
          unfocusedBorderColor = CardBorder,
          focusedTextColor = PrimaryText,
          unfocusedTextColor = PrimaryText
        ),
        modifier = Modifier.weight(1f)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = {
          if (customTag.isNotBlank()) onSelectTag(customTag.trim())
        },
        enabled = customTag.isNotBlank(),
        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = OledBlack)
      ) {
        Text("Set")
      }
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}

@Composable
private fun FocusHistorySheetContent(
  sessions: List<FocusSession>,
  onDismiss: () -> Unit
) {
  val totalMinutesToday = sessions.sumOf { it.durationMinutes }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Focus History",
        color = PrimaryText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Summary Card
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkCharcoalElevated)
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "TOTAL FOCUS", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
          text = "${totalMinutesToday}m",
          color = AmberAccent,
          fontSize = 26.sp,
          fontFamily = RobotoMonoFontFamily,
          fontWeight = FontWeight.Bold
        )
      }
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "SESSIONS", color = SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(
          text = "${sessions.size}",
          color = PrimaryText,
          fontSize = 26.sp,
          fontFamily = RobotoMonoFontFamily,
          fontWeight = FontWeight.Bold
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "RECENT SESSIONS",
      color = SecondaryText,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn(
      modifier = Modifier.height(240.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      if (sessions.isEmpty()) {
        item {
          Text(
            text = "No focus sessions completed yet.",
            color = SecondaryText,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 20.dp)
          )
        }
      }
      items(sessions) { session ->
        val dateFormat = SimpleDateFormat("h:mm a, MMM d", Locale.getDefault())
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCharcoalElevated)
            .padding(horizontal = 14.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = AmberAccent,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(text = session.activityTag, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
              Text(
                text = dateFormat.format(Date(session.completedTimestamp)),
                color = SecondaryText,
                fontSize = 11.sp
              )
            }
          }
          Text(
            text = "+${session.durationMinutes} min",
            color = AmberAccent,
            fontSize = 15.sp,
            fontFamily = RobotoMonoFontFamily,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
