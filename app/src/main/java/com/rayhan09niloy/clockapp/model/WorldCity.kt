package com.rayhan09niloy.clockapp.model

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

data class WorldCity(
  val id: String,
  val name: String,
  val country: String,
  val timeZoneId: String,
  val temperatureF: Int,
  val weatherIcon: String
) {
  fun getCurrentTime(is24Hour: Boolean): Pair<String, String> {
    val tz = TimeZone.getTimeZone(timeZoneId)
    val cal = Calendar.getInstance(tz)
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val amPm = if (hour >= 12) "pm" else "am"

    val timeStr = if (is24Hour) {
      String.format(Locale.US, "%02d:%02d", hour, minute)
    } else {
      val h = when (hour) {
        0 -> 12
        in 1..12 -> hour
        else -> hour - 12
      }
      String.format(Locale.US, "%02d:%02d", h, minute)
    }
    return Pair(timeStr, if (is24Hour) "" else amPm)
  }

  fun getTimeDifference(): String {
    val localTz = TimeZone.getDefault()
    val cityTz = TimeZone.getTimeZone(timeZoneId)
    val now = System.currentTimeMillis()
    val localOffset = localTz.getOffset(now)
    val cityOffset = cityTz.getOffset(now)
    val diffMillis = cityOffset - localOffset
    val diffHours = TimeUnit.MILLISECONDS.toHours(diffMillis.toLong())
    val diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis.toLong()) % 60

    return when {
      diffHours == 0L && diffMinutes == 0L -> "Same time"
      diffHours >= 0 -> String.format(Locale.US, "+%d:%02d hrs", diffHours, Math.abs(diffMinutes))
      else -> String.format(Locale.US, "-%d:%02d hrs", Math.abs(diffHours), Math.abs(diffMinutes))
    }
  }

  fun getDayComparison(): String {
    val localCal = Calendar.getInstance()
    val cityCal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))
    val localDay = localCal.get(Calendar.DAY_OF_YEAR)
    val cityDay = cityCal.get(Calendar.DAY_OF_YEAR)
    return when {
      cityDay > localDay -> "Tomorrow"
      cityDay < localDay -> "Yesterday"
      else -> "Today"
    }
  }
}
