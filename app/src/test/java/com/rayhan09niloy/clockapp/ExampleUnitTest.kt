package com.rayhan09niloy.clockapp

import com.rayhan09niloy.clockapp.model.AlarmItem
import com.rayhan09niloy.clockapp.model.WorldCity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAlarmItemTimeFormatting() {
    val alarm24 = AlarmItem(hour = 7, minute = 5, label = "Morning")
    assertEquals("07:05", alarm24.getFormattedTime(is24Hour = true))
    assertEquals("07:05", alarm24.getFormattedTime(is24Hour = false))
    assertEquals("AM", alarm24.getAmPm())

    val alarmEvening = AlarmItem(hour = 21, minute = 30, label = "Night")
    assertEquals("21:30", alarmEvening.getFormattedTime(is24Hour = true))
    assertEquals("09:30", alarmEvening.getFormattedTime(is24Hour = false))
    assertEquals("PM", alarmEvening.getAmPm())
  }

  @Test
  fun testAlarmDaysDisplay() {
    val alarmOnce = AlarmItem(hour = 8, minute = 0, days = emptySet())
    assertEquals("Once", alarmOnce.getDaysDisplay())

    val alarmWeekdays = AlarmItem(
      hour = 8,
      minute = 0,
      days = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)
    )
    assertEquals("Weekdays", alarmWeekdays.getDaysDisplay())

    val alarmDaily = AlarmItem(
      hour = 8,
      minute = 0,
      days = setOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)
    )
    assertEquals("Every day", alarmDaily.getDaysDisplay())
  }

  @Test
  fun testWorldCityTime() {
    val city = WorldCity("tok", "Tokyo", "Japan", "Asia/Tokyo", 64, "clear")
    val (timeStr, _) = city.getCurrentTime(is24Hour = true)
    assertTrue(timeStr.matches(Regex("\\d{2}:\\d{2}")))
  }
}
