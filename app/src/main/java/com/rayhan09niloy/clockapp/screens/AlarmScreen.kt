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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.WheelPicker
import com.rayhan09niloy.clockapp.model.AlarmItem
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.FlipDigitStyle
import com.rayhan09niloy.clockapp.ui.theme.FlapBone
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaLabelStyle
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber
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

  var showNewAlarmSheet by remember { mutableStateOf(false) }
  var alarmToEdit by remember { mutableStateOf<AlarmItem?>(null) }

  val enabledAlarms = alarms.filter { it.isEnabled }
  val disabledAlarms = alarms.filter { !it.isEnabled }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 64.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Alarms",
        color = FlapBone,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Alarm,
          contentDescription = "Next alarm",
          tint = if (enabledAlarms.isNotEmpty()) SignalAmber else MetaMuted,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = nextAlarmText,
          style = MetaLabelStyle,
          color = if (enabledAlarms.isNotEmpty()) SignalAmber else MetaMuted,
          fontSize = 13.sp
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      LazyColumn(
        modifier = Modifier.weight(1f)
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
                  tint = MetaMuted.copy(alpha = 0.5f),
                  modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "No alarms set",
                  style = MetaLabelStyle,
                  fontSize = 15.sp
                )
              }
            }
          }
        }

        // Active Alarms
        items(enabledAlarms, key = { it.id }) { alarm ->
          AlarmRow(
            alarm = alarm,
            is24Hour = settings.is24Hour,
            onToggle = { enabled -> viewModel.toggleAlarm(alarm.id, enabled) },
            onClick = { alarmToEdit = alarm },
            onTestTrigger = { viewModel.triggerAlarmAlert(alarm) }
          )
        }

        // Inactive Alarms Section (untracked sentence-case header Floor #9)
        if (disabledAlarms.isNotEmpty()) {
          item {
            Text(
              text = "Inactive alarms",
              style = MetaLabelStyle,
              color = MetaMuted,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
          }
          items(disabledAlarms, key = { it.id }) { alarm ->
            AlarmRow(
              alarm = alarm,
              is24Hour = settings.is24Hour,
              onToggle = { enabled -> viewModel.toggleAlarm(alarm.id, enabled) },
              onClick = { alarmToEdit = alarm },
              onTestTrigger = { viewModel.triggerAlarmAlert(alarm) }
            )
          }
        }
      }
    }

    // Add Alarm FAB
    FloatingActionButton(
      onClick = {
        alarmToEdit = null
        showNewAlarmSheet = true
      },
      containerColor = SignalAmber,
      contentColor = CanvasBlack,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 72.dp, end = 4.dp)
        .testTag("add_alarm_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add alarm", modifier = Modifier.size(24.dp))
    }

    // New / Edit Alarm Modal Bottom Sheet
    if (showNewAlarmSheet || alarmToEdit != null) {
      ModalBottomSheet(
        onDismissRequest = {
          showNewAlarmSheet = false
          alarmToEdit = null
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CanvasBlack,
        scrimColor = CanvasBlack.copy(alpha = 0.8f)
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
        onDismissRequest = {},
        containerColor = CanvasBlack,
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SignalAmber)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = alarm.label, color = FlapBone, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column {
            Text(
              text = alarm.getFormattedTime(settings.is24Hour) + " " + alarm.getAmPm(),
              color = SignalAmber,
              fontSize = 38.sp,
              style = FlipDigitStyle
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Ringtone: ${alarm.ringtone}",
              style = MetaLabelStyle,
              color = MetaMuted
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { viewModel.dismissCurrentAlarm() },
            colors = ButtonDefaults.buttonColors(containerColor = SignalAmber, contentColor = CanvasBlack)
          ) {
            Text("Dismiss", fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(
            onClick = { viewModel.snoozeCurrentAlarm() }
          ) {
            Text("Snooze (${alarm.snoozeMinutes}m)", color = SignalAmber)
          }
        }
      )
    }
  }
}

@Composable
private fun AlarmRow(
  alarm: AlarmItem,
  is24Hour: Boolean,
  onToggle: (Boolean) -> Unit,
  onClick: () -> Unit,
  onTestTrigger: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.Bottom) {
          Text(
            text = alarm.getFormattedTime(is24Hour),
            color = if (alarm.isEnabled) FlapBone else MetaMuted,
            style = FlipDigitStyle.copy(fontSize = 32.sp)
          )
          if (!is24Hour) {
            Text(
              text = " " + alarm.getAmPm(),
              style = MetaLabelStyle,
              color = if (alarm.isEnabled) SignalAmber else MetaMuted,
              modifier = Modifier.padding(bottom = 2.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${alarm.label} • ${alarm.getDaysDisplay()}",
          style = MetaLabelStyle,
          color = MetaMuted,
          fontSize = 12.sp
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onTestTrigger,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Test alarm",
            tint = MetaMuted,
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Switch(
          checked = alarm.isEnabled,
          onCheckedChange = onToggle,
          colors = SwitchDefaults.colors(
            checkedThumbColor = SignalAmber,
            checkedTrackColor = SignalAmber.copy(alpha = 0.35f),
            uncheckedThumbColor = MetaMuted,
            uncheckedTrackColor = HairlineDivider
          ),
          modifier = Modifier.testTag("alarm_switch_${alarm.id}")
        )
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(HairlineDivider)
    )
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
  val initialAmPm = if (initialHour >= 12) 1 else 0

  var selectedHourIndex by remember {
    mutableIntStateOf(
      if (is24Hour) initialHour
      else when (val h = initialHour % 12) {
        0 -> 11
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
  var flipToSnooze by remember { mutableStateOf(initialAlarm?.flipToSnooze ?: true) }

  val hoursList = remember(is24Hour) {
    if (is24Hour) (0..23).map { String.format("%02d", it) }
    else (1..12).map { String.format("%02d", it) }
  }
  val minutesList = remember { (0..59).map { String.format("%02d", it) } }
  val amPmList = remember { listOf("am", "pm") }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 8.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = MetaMuted)
      }
      Text(
        text = if (initialAlarm != null) "Edit alarm" else "New alarm",
        color = FlapBone,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
      Button(
        onClick = {
          val calculatedHour = if (is24Hour) {
            selectedHourIndex
          } else {
            val h12 = selectedHourIndex + 1
            if (selectedAmPmIndex == 1) {
              if (h12 == 12) 12 else h12 + 12
            } else {
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
            snoozeLimit = "3 times",
            flipToSnooze = flipToSnooze
          )
          onSaveAlarm(item)
        },
        colors = ButtonDefaults.buttonColors(containerColor = SignalAmber, contentColor = CanvasBlack),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Save", fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, HairlineDivider, RoundedCornerShape(16.dp))
        .padding(vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        WheelPicker(
          items = hoursList,
          selectedIndex = selectedHourIndex,
          onSelectedIndexChanged = { selectedHourIndex = it },
          itemWidth = 72.dp
        )

        Text(
          text = ":",
          color = SignalAmber,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 8.dp)
        )

        WheelPicker(
          items = minutesList,
          selectedIndex = selectedMinuteIndex,
          onSelectedIndexChanged = { selectedMinuteIndex = it },
          itemWidth = 72.dp
        )

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

    OutlinedTextField(
      value = label,
      onValueChange = { label = it },
      label = { Text("Alarm label", color = MetaMuted) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SignalAmber,
        unfocusedBorderColor = HairlineDivider,
        focusedTextColor = FlapBone,
        unfocusedTextColor = FlapBone,
        focusedContainerColor = CanvasBlack,
        unfocusedContainerColor = CanvasBlack
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = "Repeat",
      style = MetaLabelStyle,
      color = MetaMuted,
      fontSize = 12.sp
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
            .border(1.dp, if (selected) SignalAmber else HairlineDivider, RoundedCornerShape(8.dp))
            .background(if (selected) SignalAmber else CanvasBlack)
            .clickable { repeatMode = mode }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mode,
            color = if (selected) CanvasBlack else FlapBone,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
        }
      }
    }

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
              .background(if (isDaySelected) SignalAmber else CanvasBlack)
              .border(1.dp, if (isDaySelected) SignalAmber else HairlineDivider, CircleShape)
              .clickable {
                selectedDays = if (isDaySelected) selectedDays - calDay else selectedDays + calDay
              },
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = letter,
              color = if (isDaySelected) CanvasBlack else FlapBone,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "Sound & vibration",
      style = MetaLabelStyle,
      color = MetaMuted,
      fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Slider(
        value = volume,
        onValueChange = { volume = it },
        colors = SliderDefaults.colors(
          thumbColor = SignalAmber,
          activeTrackColor = SignalAmber,
          inactiveTrackColor = HairlineDivider
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }

    SheetToggle(
      title = "Ascending volume",
      subtitle = "Gradually increases volume over 30s",
      checked = ascendingVolume,
      onCheckedChange = { ascendingVolume = it }
    )

    SheetToggle(
      title = "Vibrate",
      subtitle = "Vibrate on alarm trigger",
      checked = vibrate,
      onCheckedChange = { vibrate = it }
    )

    SheetToggle(
      title = "Auto-delete",
      subtitle = "Delete alarm after it turns off",
      checked = autoDelete,
      onCheckedChange = { autoDelete = it }
    )

    SheetToggle(
      title = "Flip phone to snooze",
      subtitle = "Turn phone face down on table to snooze",
      checked = flipToSnooze,
      onCheckedChange = { flipToSnooze = it }
    )

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
      Text(text = title, color = FlapBone, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      Text(text = subtitle, style = MetaLabelStyle, fontSize = 11.sp)
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = SignalAmber,
        checkedTrackColor = SignalAmber.copy(alpha = 0.35f),
        uncheckedThumbColor = MetaMuted,
        uncheckedTrackColor = HairlineDivider
      )
    )
  }
}
