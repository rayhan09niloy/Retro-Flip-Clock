package com.rayhan09niloy.clockapp.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.ui.theme.AmberAccent
import com.rayhan09niloy.clockapp.ui.theme.AntonFontFamily
import com.rayhan09niloy.clockapp.ui.theme.BebasNeueFontFamily
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoalElevated
import com.rayhan09niloy.clockapp.ui.theme.OledBlack
import com.rayhan09niloy.clockapp.ui.theme.PrimaryText
import com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
import com.rayhan09niloy.clockapp.ui.theme.SecondaryText
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeskClockScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val settings by viewModel.settings.collectAsState()
  val hours by viewModel.currentHours.collectAsState()
  val minutes by viewModel.currentMinutes.collectAsState()
  val seconds by viewModel.currentSeconds.collectAsState()
  val amPm by viewModel.currentAmPm.collectAsState()
  val weekday by viewModel.currentWeekdayFormatted.collectAsState()
  val dateFormatted by viewModel.currentDateFormatted.collectAsState()
  val batteryPercent by viewModel.batteryPercent.collectAsState()
  val isCharging by viewModel.isCharging.collectAsState()
  val nextAlarmText by viewModel.nextAlarmText.collectAsState()
  val sleepTimerSecondsLeft by viewModel.sleepTimerSecondsLeft.collectAsState()

  var showSettingsSheet by remember { mutableStateOf(false) }
  var isAmbientDimmed by remember { mutableStateOf(false) }

  val currentFontFamily = when (settings.fontStyle) {
    "Anton" -> AntonFontFamily
    "Roboto Mono" -> RobotoMonoFontFamily
    else -> BebasNeueFontFamily
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(if (isAmbientDimmed) Color(0xFF000000) else OledBlack)
      .statusBarsPadding()
      .padding(horizontal = 14.dp, vertical = 6.dp)
  ) {
    val isLandscape = maxWidth > maxHeight

    // Responsive dimensions: numbers take all the space!
    val cardWidth = if (isLandscape) {
      ((maxWidth - 100.dp) / 2).coerceIn(180.dp, 380.dp)
    } else {
      (maxWidth - 44.dp).coerceIn(240.dp, 400.dp)
    }

    val cardHeight = if (isLandscape) {
      (maxHeight - 88.dp).coerceIn(160.dp, 280.dp)
    } else {
      ((maxHeight - 190.dp) / 2).coerceIn(150.dp, 240.dp)
    }

    // Front text takes ALL space inside the card
    val fontSize = (cardHeight.value * 0.94f).sp

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // --- Top Status Bar ---
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          if (settings.showWeekday) {
            Text(
              text = weekday.uppercase(),
              color = AmberAccent,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.2.sp
            )
          }
          if (settings.showDate) {
            Text(
              text = dateFormatted,
              color = PrimaryText,
              fontSize = if (isLandscape) 13.sp else 14.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        // Alarm banner in landscape or portrait
        if (settings.showNextAlarm && nextAlarmText != "No upcoming alarms") {
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .background(DarkCharcoalElevated)
              .border(0.5.dp, CardBorder, RoundedCornerShape(14.dp))
              .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Alarm,
              contentDescription = "Next Alarm",
              tint = AmberAccent,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = nextAlarmText,
              color = AmberAccent,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (settings.showWeather) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(DarkCharcoal)
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather",
                tint = AmberAccent,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "72°F",
                color = PrimaryText,
                fontSize = 11.sp,
                fontFamily = RobotoMonoFontFamily
              )
            }
          }

          if (settings.showBattery) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(DarkCharcoal)
                .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
              Icon(
                imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                contentDescription = "Battery",
                tint = if (isCharging) AmberAccent else SecondaryText,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "$batteryPercent%",
                color = PrimaryText,
                fontSize = 11.sp,
                fontFamily = RobotoMonoFontFamily
              )
            }
          }
        }
      }

      // --- Center Display Area (Adaptive Landscape & Portrait) ---
      Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center
      ) {
        if (isLandscape) {
          // LANDSCAPE: SIDE BY SIDE (Hours on Left, Minutes on Right)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            FlipCard(
              value = hours,
              cardWidth = cardWidth,
              cardHeight = cardHeight,
              fontSize = fontSize,
              fontFamily = currentFontFamily,
              showSplitLine = settings.showSplitLine,
              animateFlip = settings.flipAnimationEnabled,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )

            // Side Indicators in Landscape
            Column(
              modifier = Modifier
                .height(cardHeight)
                .padding(horizontal = 8.dp),
              verticalArrangement = Arrangement.SpaceEvenly,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              if (!settings.is24Hour) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .border(0.5.dp, AmberAccent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = amPm,
                    color = AmberAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                  )
                }
              }

              if (settings.showSeconds) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .border(0.5.dp, CardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = "SEC",
                    color = SecondaryText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = seconds,
                    color = AmberAccent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                  )
                }
              }
            }

            FlipCard(
              value = minutes,
              cardWidth = cardWidth,
              cardHeight = cardHeight,
              fontSize = fontSize,
              fontFamily = currentFontFamily,
              showSplitLine = settings.showSplitLine,
              animateFlip = settings.flipAnimationEnabled,
              onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
            )
          }
        } else {
          // PORTRAIT: STACKED (Hours on Top, Minutes on Bottom)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              FlipCard(
                value = hours,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )

              FlipCard(
                value = minutes,
                cardWidth = cardWidth,
                cardHeight = cardHeight,
                fontSize = fontSize,
                fontFamily = currentFontFamily,
                showSplitLine = settings.showSplitLine,
                animateFlip = settings.flipAnimationEnabled,
                onFlipSound = { viewModel.soundHelper.playMechanicalClick() }
              )
            }

            Spacer(modifier = Modifier.width(10.dp))
            Column(
              modifier = Modifier.height(cardHeight * 2 + 10.dp),
              verticalArrangement = Arrangement.SpaceBetween,
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              if (!settings.is24Hour) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCharcoal)
                    .border(0.5.dp, AmberAccent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = amPm,
                    color = AmberAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                  )
                }
              } else {
                Spacer(modifier = Modifier.height(8.dp))
              }

              if (settings.showSeconds) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCharcoal)
                    .border(0.5.dp, CardBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                  Text(
                    text = "SEC",
                    color = SecondaryText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = seconds,
                    color = AmberAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = currentFontFamily
                  )
                }
              }
            }
          }
        }
      }

      // --- Bottom Floating Dock ---
      Row(
        modifier = Modifier
          .padding(bottom = 6.dp)
          .clip(RoundedCornerShape(22.dp))
          .background(DarkCharcoal)
          .border(0.5.dp, CardBorder, RoundedCornerShape(22.dp))
          .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { isAmbientDimmed = !isAmbientDimmed },
          modifier = Modifier.size(34.dp).testTag("ambient_dim_btn")
        ) {
          Icon(
            imageVector = Icons.Default.DarkMode,
            contentDescription = "Dim Display",
            tint = if (isAmbientDimmed) AmberAccent else SecondaryText,
            modifier = Modifier.size(17.dp)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (settings.is24Hour) AmberAccent else Color.Transparent)
            .clickable {
              viewModel.updateSettings { it.copy(is24Hour = !it.is24Hour) }
            }
            .padding(horizontal = 9.dp, vertical = 4.dp)
            .testTag("toggle_24h_btn")
        ) {
          Text(
            text = "24H",
            color = if (settings.is24Hour) OledBlack else SecondaryText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        if (sleepTimerSecondsLeft > 0) {
          val minLeft = (sleepTimerSecondsLeft / 60) + 1
          Text(
            text = "Sleep: ${minLeft}m",
            color = AmberAccent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        IconButton(
          onClick = { showSettingsSheet = true },
          modifier = Modifier.size(34.dp).testTag("clock_settings_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Clock Settings",
            tint = AmberAccent,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // --- Settings Modal Bottom Sheet ---
    if (showSettingsSheet) {
      ModalBottomSheet(
        onDismissRequest = { showSettingsSheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkCharcoal,
        scrimColor = Color.Black.copy(alpha = 0.7f)
      ) {
        ClockSettingsSheetContent(
          settings = settings,
          onUpdateSettings = { viewModel.updateSettings(it) },
          onSetSleepTimer = { viewModel.setSleepTimer(it) },
          onDismiss = { showSettingsSheet = false }
        )
      }
    }
  }
}

@Composable
private fun ClockSettingsSheetContent(
  settings: com.rayhan09niloy.clockapp.model.ClockSettings,
  onUpdateSettings: ((com.rayhan09niloy.clockapp.model.ClockSettings) -> com.rayhan09niloy.clockapp.model.ClockSettings) -> Unit,
  onSetSleepTimer: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 10.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Clock Settings",
        color = PrimaryText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Animation & Time Format Section
    SettingsSectionHeader("APPEARANCE & DISPLAY")
    SettingsToggleItem(
      title = "Flip animation",
      subtitle = "Realistic mechanical flap with impact flex",
      isChecked = settings.flipAnimationEnabled,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(flipAnimationEnabled = checked) } }
    )
    SettingsToggleItem(
      title = "24-Hour time",
      subtitle = "Switch between 12-hour and 24-hour mode",
      isChecked = settings.is24Hour,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(is24Hour = checked) } }
    )
    SettingsToggleItem(
      title = "Leading zero",
      subtitle = "Display 02 vs 2 for single-digit hours",
      isChecked = settings.leadingZero,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(leadingZero = checked) } }
    )

    Spacer(modifier = Modifier.height(10.dp))
    Text(
      text = "Typeface style",
      color = PrimaryText,
      fontSize = 15.sp,
      fontWeight = FontWeight.Medium
    )
    Text(
      text = "Select bold energetic clock font",
      color = SecondaryText,
      fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("Bebas Neue" to "Bebas Neue", "Anton" to "Anton", "Roboto Mono" to "Mono").forEach { (fontKey, fontLabel) ->
        val selected = settings.fontStyle == fontKey
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) AmberAccent else DarkCharcoalElevated)
            .border(1.dp, if (selected) AmberAccent else CardBorder, RoundedCornerShape(8.dp))
            .clickable { onUpdateSettings { it.copy(fontStyle = fontKey) } }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = fontLabel,
            color = if (selected) OledBlack else PrimaryText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Visible Elements Section
    SettingsSectionHeader("SHOW / HIDE ITEMS")
    SettingsToggleItem(
      title = "Split seam line",
      subtitle = "Horizontal slit across center of cards",
      isChecked = settings.showSplitLine,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(showSplitLine = checked) } }
    )
    SettingsToggleItem(
      title = "Seconds counter",
      subtitle = "Small seconds ticker on the side",
      isChecked = settings.showSeconds,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(showSeconds = checked) } }
    )
    SettingsToggleItem(
      title = "Weekday & Date",
      subtitle = "Show current day and full date header",
      isChecked = settings.showWeekday && settings.showDate,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(showWeekday = checked, showDate = checked) } }
    )
    SettingsToggleItem(
      title = "Next alarm",
      subtitle = "Upcoming alarm countdown banner",
      isChecked = settings.showNextAlarm,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(showNextAlarm = checked) } }
    )
    SettingsToggleItem(
      title = "Battery & Weather",
      subtitle = "Display percentage and local temperature",
      isChecked = settings.showBattery && settings.showWeather,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(showBattery = checked, showWeather = checked) } }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Audio & Sleep Timer Section
    SettingsSectionHeader("SOUND & SLEEP")
    SettingsToggleItem(
      title = "Ticking sound",
      subtitle = "Subtle mechanical click on each second",
      isChecked = settings.tickingSound,
      onCheckedChange = { checked -> onUpdateSettings { it.copy(tickingSound = checked) } }
    )

    Spacer(modifier = Modifier.height(12.dp))
    Text(
      text = "Sleep timer (Auto close)",
      color = PrimaryText,
      fontSize = 15.sp,
      fontWeight = FontWeight.Medium
    )
    Text(
      text = "Turn off app display after selected duration",
      color = SecondaryText,
      fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(0 to "Off", 15 to "15m", 30 to "30m", 45 to "45m", 60 to "60m").forEach { (mins, label) ->
        val selected = settings.sleepTimerMinutes == mins
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) AmberAccent else DarkCharcoalElevated)
            .border(1.dp, if (selected) AmberAccent else CardBorder, RoundedCornerShape(8.dp))
            .clickable { onSetSleepTimer(mins) }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            color = if (selected) OledBlack else PrimaryText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(32.dp))
  }
}

@Composable
private fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    color = AmberAccent,
    fontSize = 12.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(vertical = 8.dp)
  )
}

@Composable
private fun SettingsToggleItem(
  title: String,
  subtitle: String,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = title, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      Text(text = subtitle, color = SecondaryText, fontSize = 11.sp)
    }
    Switch(
      checked = isChecked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = AmberAccent,
        checkedTrackColor = AmberAccent.copy(alpha = 0.35f),
        uncheckedThumbColor = SecondaryText,
        uncheckedTrackColor = DarkCharcoalElevated
      )
    )
  }
}
