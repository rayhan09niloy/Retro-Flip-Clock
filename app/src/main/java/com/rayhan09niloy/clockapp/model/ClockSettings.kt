package com.rayhan09niloy.clockapp.model

data class ClockSettings(
  val flipAnimationEnabled: Boolean = true,
  val is24Hour: Boolean = false,
  val leadingZero: Boolean = true,
  val showSplitLine: Boolean = true,
  val showSeconds: Boolean = true,
  val showWeekday: Boolean = true,
  val showDate: Boolean = true,
  val showNextAlarm: Boolean = true,
  val showBattery: Boolean = true,
  val showWeather: Boolean = true,
  val tickingSound: Boolean = false,
  val sleepTimerMinutes: Int = 0,
  val sleepTimerEndTime: Long? = null,
  val fontStyle: String = "SansSerif",
  val ambientTheme: String = "OLED Black"
)
