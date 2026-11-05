package com.rayhan09niloy.clockapp.model

import java.util.Calendar
import java.util.UUID

data class AlarmItem(
  val id: String = UUID.randomUUID().toString(),
  val hour: Int, // 0-23
  val minute: Int, // 0-59
  val label: String = "Alarm",
  val isEnabled: Boolean = true,
  val days: Set<Int> = emptySet(), // Calendar.SUNDAY(1) to Calendar.SATURDAY(7). empty means once.
  val ringtone: String = "Classic Bell",
  val vibrate: Boolean = true,
  val volume: Float = 0.8f,
  val ascendingVolume: Boolean = true,
  val autoDelete: Boolean = false,
  val snoozeMinutes: Int = 10,
  val snoozeLimit: String = "3 times",
  val flipToSnooze: Boolean = true
) {
  fun getFormattedTime(is24Hour: Boolean): String {
    return if (is24Hour) {
      String.format("%02d:%02d", hour, minute)
    } else {
      val displayHour = when (hour) {
        0 -> 12
        in 1..12 -> hour
        else -> hour - 12
      }
      String.format("%02d:%02d", displayHour, minute)
    }
  }

  fun getAmPm(): String {
    return if (hour >= 12) "pm" else "am"
  }

  fun getDaysDisplay(): String {
    if (days.isEmpty()) return "Once"
    if (days.size == 7) return "Every day"
    if (days == setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)) {
      return "Weekdays"
    }
    if (days == setOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
      return "Weekends"
    }
    val names = mapOf(
      Calendar.SUNDAY to "Sun",
      Calendar.MONDAY to "Mon",
      Calendar.TUESDAY to "Tue",
      Calendar.WEDNESDAY to "Wed",
      Calendar.THURSDAY to "Thu",
      Calendar.FRIDAY to "Fri",
      Calendar.SATURDAY to "Sat"
    )
    return listOf(
      Calendar.SUNDAY,
      Calendar.MONDAY,
      Calendar.TUESDAY,
      Calendar.WEDNESDAY,
      Calendar.THURSDAY,
      Calendar.FRIDAY,
      Calendar.SATURDAY
    ).filter { days.contains(it) }
      .joinToString(", ") { names[it] ?: "" }
  }

  fun millisUntilNextAlarm(): Long {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, hour)
      set(Calendar.MINUTE, minute)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    if (days.isEmpty()) {
      if (target.timeInMillis <= now.timeInMillis) {
        target.add(Calendar.DAY_OF_YEAR, 1)
      }
      return target.timeInMillis - now.timeInMillis
    }

    for (dayOffset in 0..7) {
      val checkCal = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, dayOffset)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
      }
      val dayOfWeek = checkCal.get(Calendar.DAY_OF_WEEK)
      if (days.contains(dayOfWeek) && checkCal.timeInMillis > now.timeInMillis) {
        return checkCal.timeInMillis - now.timeInMillis
      }
    }
    return Long.MAX_VALUE
  }
}
