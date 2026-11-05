package com.rayhan09niloy.clockapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.rayhan09niloy.clockapp.components.WheelPicker
import com.rayhan09niloy.clockapp.model.AlarmItem
import com.rayhan09niloy.clockapp.ui.theme.AmberAccent
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoalElevated
import com.rayhan09niloy.clockapp.ui.theme.OledBlack
import com.rayhan09niloy.clockapp.ui.theme.PrimaryText
import com.rayhan09niloy.clockapp.ui.theme.RedAccent
import com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
import com.rayhan09niloy.clockapp.ui.theme.SecondaryText
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val alarms by viewModel.alarms.collectAsState()
  val nextAlarmText by viewModel.nextAlarmText.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val triggeringAlarm by viewModel.triggeringAlarm.collectAsState()

  val currentFontFamily = when (settings.fontStyle) {
    "Anton" -> com.rayhan09niloy.clockapp.ui.theme.AntonFontFamily
    "Roboto Mono" -> com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
    else -> com.rayhan09niloy.clockapp.ui.theme.BebasNeueFontFamily
  }

  var showNewAlarmSheet by remember { mutableStateOf(false) }
  var alarmToEdit by remember { mutableStateOf<AlarmItem?>(null) }

  val enabledAlarms = alarms.filter { it.isEnabled }
  val disabledAlarms = alarms.filter { !it.isEnabled }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(OledBlack)
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 76.dp)
    ) {
      // --- Top Area: Next Active Alarm Text ---
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Alarms",
        color = PrimaryText,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Alarm,
          contentDescription = "Next Alarm",
          tint = if (enabledAlarms.isNotEmpty()) AmberAccent else SecondaryText,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = nextAlarmText,
          color = if (enabledAlarms.isNotEmpty()) AmberAccent else SecondaryText,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // --- Alarms List ---
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (enabledAlarms.isEmpty() && disabledAlarms.isEmpty()) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.Alarm,
                  contentDescription = "No alarms",
                  tint = SecondaryText.copy(alpha = 0.5f),
                  modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "No alarms set",
                  color = SecondaryText,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Medium
                )
                Text(
                  text = "Tap the + button to create a new alarm",
                  color = SecondaryText.copy(alpha = 0.7f),
                  fontSize = 13.sp
                )
              }
            }
          }
        }

        // Active Alarms
        if (enabledAlarms.isNotEmpty()) {
          items(enabledAlarms, key = { it.id }) { alarm ->
            AlarmCard(
              alarm = alarm,
              is24Hour = settings.is24Hour,
              fontFamily = currentFontFamily,
              onToggle = { enabled -> viewModel.toggleAlarm(alarm.id, enabled) },
              onClick = { alarmToEdit = alarm },
              onDelete = { viewModel.deleteAlarm(alarm.id) },
              onTestTrigger = { viewModel.triggerAlarmAlert(alarm) }
            )
          }
        }

        // Disabled Alarms Section
        if (disabledAlarms.isNotEmpty()) {
          item {
            Text(
              text = "INACTIVE ALARMS",
              color = SecondaryText,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
          }
          items(disabledAlarms, key = { it.id }) { alarm ->
            AlarmCard(
              alarm = alarm,
              is24Hour = settings.is24Hour,
              fontFamily = currentFontFamily,
              onToggle = { enabled -> viewModel.toggleAlarm(alarm.id, enabled) },
              onClick = { alarmToEdit = alarm },
              onDelete = { viewModel.deleteAlarm(alarm.id) },
              onTestTrigger = { viewModel.triggerAlarmAlert(alarm) }
            )
          }
        }
      }
    }

    // --- Bottom Right Yellow Circular Add Button ---
    FloatingActionButton(
      onClick = {
        alarmToEdit = null
        showNewAlarmSheet = true
      },
      containerColor = AmberAccent,
      contentColor = OledBlack,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 80.dp, end = 4.dp)
        .testTag("add_alarm_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add Alarm", modifier = Modifier.size(28.dp))
    }

    // New / Edit Alarm Modal Bottom Sheet
    if (showNewAlarmSheet || alarmToEdit != null) {
      ModalBottomSheet(
        onDismissRequest = {
          showNewAlarmSheet = false
          alarmToEdit = null
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkCharcoal,
        scrimColor = Color.Black.copy(alpha = 0.7f)
      ) {
        NewAlarmSheetContent(
          initialAlarm = alarmToEdit,
          is24Hour = settings.is24Hour,
          onSaveAlarm = { alarm ->
            if (alarmToEdit != null) {
              viewModel.updateAlarm(alarm)
            } else {
              viewModel.addAlarm(alarm)
            }
            showNewAlarmSheet = false
            alarmToEdit = null
          },
          onDismiss = {
            showNewAlarmSheet = false
            alarmToEdit = null
          }
        )
      }
    }

    // Triggering Alarm Alert Dialog
    if (triggeringAlarm != null) {
      val alarm = triggeringAlarm!!
      AlertDialog(
        onDismissRequest = { /* dismiss requires user button tap or face-down flip */ },
        containerColor = DarkCharcoal,
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AmberAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = alarm.label, color = PrimaryText, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column {
            Text(
              text = alarm.getFormattedTime(settings.is24Hour) + " " + alarm.getAmPm(),
              color = AmberAccent,
              fontSize = 42.sp,
              fontFamily = currentFontFamily,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Ringtone: ${alarm.ringtone}",
              color = SecondaryText,
              fontSize = 14.sp
            )
            if (alarm.flipToSnooze) {
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ScreenRotation, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Flip phone face down to snooze",
                  color = AmberAccent,
                  fontSize = 12.sp
                )
              }
            }
          }
        },
        confirmButton = {
          Button(
            onClick = { viewModel.dismissCurrentAlarm() },
            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = OledBlack)
          ) {
            Text("Dismiss", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(
            onClick = { viewModel.snoozeCurrentAlarm() }
          ) {
            Text("Snooze (${alarm.snoozeMinutes}m)", color = AmberAccent)
          }
        }
      )
    }
  }
}

@Composable
private fun AlarmCard(
  alarm: AlarmItem,
  is24Hour: Boolean,
  fontFamily: androidx.compose.ui.text.font.FontFamily,
  onToggle: (Boolean) -> Unit,
  onClick: () -> Unit,
  onDelete: () -> Unit,
  onTestTrigger: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .border(1.dp, if (alarm.isEnabled) CardBorder else Color(0xFF1E1E22), RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(
      containerColor = if (alarm.isEnabled) DarkCharcoal else Color(0xFF121214)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = alarm.getFormattedTime(is24Hour),
            color = if (alarm.isEnabled) PrimaryText else SecondaryText.copy(alpha = 0.6f),
            fontSize = 38.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold
          )
          if (!is24Hour) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = alarm.getAmPm(),
              color = if (alarm.isEnabled) AmberAccent else SecondaryText.copy(alpha = 0.5f),
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = fontFamily,
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = alarm.label,
            color = if (alarm.isEnabled) PrimaryText else SecondaryText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = " • ${alarm.getDaysDisplay()}",
            color = SecondaryText,
            fontSize = 13.sp
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Quick preview trigger button
        IconButton(
          onClick = onTestTrigger,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Test Alarm",
            tint = SecondaryText,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Amber Toggle Switch
        Switch(
          checked = alarm.isEnabled,
          onCheckedChange = onToggle,
          colors = SwitchDefaults.colors(
            checkedThumbColor = AmberAccent,
            checkedTrackColor = AmberAccent.copy(alpha = 0.35f),
            uncheckedThumbColor = SecondaryText,
            uncheckedTrackColor = DarkCharcoalElevated
          ),
          modifier = Modifier.testTag("alarm_switch_${alarm.id}")
        )
      }
    }
  }
}

@Composable
private fun NewAlarmSheetContent(
  initialAlarm: AlarmItem?,
  is24Hour: Boolean,
  onSaveAlarm: (AlarmItem) -> Unit,
  onDismiss: () -> Unit
) {
  val initialHour = initialAlarm?.hour ?: 7
  val initialMinute = initialAlarm?.minute ?: 0
  val initialAmPm = if (initialHour >= 12) 1 else 0 // 0 = AM, 1 = PM

  var selectedHourIndex by remember {
    mutableIntStateOf(
      if (is24Hour) initialHour
      else when (val h = initialHour % 12) {
        0 -> 11 // 12 is at index 11
        else -> h - 1
      }
    )
  }

  var selectedMinuteIndex by remember { mutableIntStateOf(initialMinute) }
  var selectedAmPmIndex by remember { mutableIntStateOf(initialAmPm) }

  var label by remember { mutableStateOf(initialAlarm?.label ?: "Alarm") }
  var repeatMode by remember {
    mutableStateOf(
      when {
        initialAlarm == null -> "Daily"
        initialAlarm.days.isEmpty() -> "Once"
        initialAlarm.days.size == 7 -> "Daily"
        else -> "Custom"
      }
    )
  }

  var selectedDays by remember {
    mutableStateOf(
      initialAlarm?.days ?: setOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY
      )
    )
  }

  var ringtone by remember { mutableStateOf(initialAlarm?.ringtone ?: "Classic Bell") }
  var vibrate by remember { mutableStateOf(initialAlarm?.vibrate ?: true) }
  var volume by remember { mutableFloatStateOf(initialAlarm?.volume ?: 0.8f) }
  var ascendingVolume by remember { mutableStateOf(initialAlarm?.ascendingVolume ?: true) }
  var autoDelete by remember { mutableStateOf(initialAlarm?.autoDelete ?: false) }
  var snoozeMinutes by remember { mutableIntStateOf(initialAlarm?.snoozeMinutes ?: 10) }
  var snoozeLimit by remember { mutableStateOf(initialAlarm?.snoozeLimit ?: "3 times") }
  var flipToSnooze by remember { mutableStateOf(initialAlarm?.flipToSnooze ?: true) }

  val hoursList = remember(is24Hour) {
    if (is24Hour) (0..23).map { String.format("%02d", it) }
    else (1..12).map { String.format("%02d", it) }
  }
  val minutesList = remember { (0..59).map { String.format("%02d", it) } }
  val amPmList = remember { listOf("AM", "PM") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .verticalScroll(rememberScrollState())
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = SecondaryText)
      }
      Text(
        text = if (initialAlarm != null) "Edit Alarm" else "New Alarm",
        color = PrimaryText,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Button(
        onClick = {
          val calculatedHour = if (is24Hour) {
            selectedHourIndex
          } else {
            val h12 = selectedHourIndex + 1
            if (selectedAmPmIndex == 1) { // PM
              if (h12 == 12) 12 else h12 + 12
            } else { // AM
              if (h12 == 12) 0 else h12
            }
          }

          val finalDays = when (repeatMode) {
            "Once" -> emptySet()
            "Daily" -> setOf(
              Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
              Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
            )
            else -> selectedDays
          }

          val item = AlarmItem(
            id = initialAlarm?.id ?: java.util.UUID.randomUUID().toString(),
            hour = calculatedHour,
            minute = selectedMinuteIndex,
            label = if (label.isBlank()) "Alarm" else label.trim(),
            isEnabled = true,
            days = finalDays,
            ringtone = ringtone,
            vibrate = vibrate,
            volume = volume,
            ascendingVolume = ascendingVolume,
            autoDelete = autoDelete,
            snoozeMinutes = snoozeMinutes,
            snoozeLimit = snoozeLimit,
            flipToSnooze = flipToSnooze
          )
          onSaveAlarm(item)
        },
        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = OledBlack),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Save", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // --- Time Pickers (Scroll Wheels) ---
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(DarkCharcoalElevated)
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Hours Wheel
        WheelPicker(
          items = hoursList,
          selectedIndex = selectedHourIndex,
          onSelectedIndexChanged = { selectedHourIndex = it },
          itemWidth = 72.dp
        )

        Text(
          text = ":",
          color = AmberAccent,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Minutes Wheel
        WheelPicker(
          items = minutesList,
          selectedIndex = selectedMinuteIndex,
          onSelectedIndexChanged = { selectedMinuteIndex = it },
          itemWidth = 72.dp
        )

        // AM/PM Wheel (if 12h)
        if (!is24Hour) {
          Spacer(modifier = Modifier.width(12.dp))
          WheelPicker(
            items = amPmList,
            selectedIndex = selectedAmPmIndex,
            onSelectedIndexChanged = { selectedAmPmIndex = it },
            itemWidth = 60.dp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Label input
    OutlinedTextField(
      value = label,
      onValueChange = { label = it },
      label = { Text("Alarm Label") },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AmberAccent,
        unfocusedBorderColor = CardBorder,
        focusedLabelColor = AmberAccent,
        unfocusedLabelColor = SecondaryText,
        focusedTextColor = PrimaryText,
        unfocusedTextColor = PrimaryText
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    // --- Repeat Options ---
    Text(
      text = "REPEAT",
      color = AmberAccent,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("Once", "Daily", "Custom").forEach { mode ->
        val selected = repeatMode == mode
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) AmberAccent else DarkCharcoalElevated)
            .clickable { repeatMode = mode }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mode,
            color = if (selected) OledBlack else PrimaryText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
        }
      }
    }

    // Custom Day Selector
    if (repeatMode == "Custom") {
      Spacer(modifier = Modifier.height(10.dp))
      val daysMap = listOf(
        Calendar.SUNDAY to "S",
        Calendar.MONDAY to "M",
        Calendar.TUESDAY to "T",
        Calendar.WEDNESDAY to "W",
        Calendar.THURSDAY to "T",
        Calendar.FRIDAY to "F",
        Calendar.SATURDAY to "S"
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        daysMap.forEach { (calDay, letter) ->
          val isDaySelected = selectedDays.contains(calDay)
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (isDaySelected) AmberAccent else DarkCharcoalElevated)
              .border(1.dp, if (isDaySelected) AmberAccent else CardBorder, CircleShape)
              .clickable {
                selectedDays = if (isDaySelected) selectedDays - calDay else selectedDays + calDay
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = letter,
              color = if (isDaySelected) OledBlack else PrimaryText,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // --- Options: Ringtone, Vibration, Volume, Ascending ---
    Text(
      text = "SOUND & VIBRATION",
      color = AmberAccent,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    // Ringtone Selector
    Text(text = "Ringtone", color = SecondaryText, fontSize = 12.sp)
    Spacer(modifier = Modifier.height(4.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf("Classic Bell", "Gentle Chime", "Digital Pulse").forEach { tone ->
        val isSelected = ringtone == tone
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AmberAccent.copy(alpha = 0.2f) else DarkCharcoalElevated)
            .border(1.dp, if (isSelected) AmberAccent else CardBorder, RoundedCornerShape(8.dp))
            .clickable { ringtone = tone }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = tone,
            color = if (isSelected) AmberAccent else PrimaryText,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Volume Slider
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = AmberAccent, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Slider(
        value = volume,
        onValueChange = { volume = it },
        colors = SliderDefaults.colors(
          thumbColor = AmberAccent,
          activeTrackColor = AmberAccent,
          inactiveTrackColor = DarkCharcoalElevated
        ),
        modifier = Modifier.weight(1f)
      )
    }

    // Ascending Volume Toggle
    SheetToggle(
      title = "Ascending volume",
      subtitle = "Gradually increases volume over 30s",
      checked = ascendingVolume,
      onCheckedChange = { ascendingVolume = it }
    )

    // Vibration Toggle
    SheetToggle(
      title = "Vibrate",
      subtitle = "Vibrate on alarm trigger",
      checked = vibrate,
      onCheckedChange = { vibrate = it }
    )

    // Auto Delete Toggle
    SheetToggle(
      title = "Auto-delete",
      subtitle = "Delete alarm after it turns off",
      checked = autoDelete,
      onCheckedChange = { autoDelete = it }
    )

    // Flip to Snooze Gesture Toggle
    SheetToggle(
      title = "Flip phone to snooze",
      subtitle = "Turn phone face down on table to snooze",
      checked = flipToSnooze,
      onCheckedChange = { flipToSnooze = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Snooze Duration
    Text(
      text = "SNOOZE RULES",
      color = AmberAccent,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf(5 to "5 min", 10 to "10 min", 15 to "15 min").forEach { (mins, label) ->
        val selected = snoozeMinutes == mins
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) AmberAccent else DarkCharcoalElevated)
            .border(1.dp, if (selected) AmberAccent else CardBorder, RoundedCornerShape(8.dp))
            .clickable { snoozeMinutes = mins }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = label,
            color = if (selected) OledBlack else PrimaryText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(36.dp))
  }
}

@Composable
private fun SheetToggle(
  title: String,
  subtitle: String,
  checked: Boolean,
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
      checked = checked,
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
