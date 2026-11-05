package com.rayhan09niloy.clockapp.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rayhan09niloy.clockapp.model.AlarmItem
import com.rayhan09niloy.clockapp.model.ClockSettings
import com.rayhan09niloy.clockapp.model.FocusSession
import com.rayhan09niloy.clockapp.model.StopwatchLap
import com.rayhan09niloy.clockapp.model.WorldCity
import com.rayhan09niloy.clockapp.util.FlipGestureDetector
import com.rayhan09niloy.clockapp.util.SoundHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClockViewModel(application: Application) : AndroidViewModel(application) {
  private val prefs: SharedPreferences =
    application.getSharedPreferences("retro_flip_clock_prefs", Context.MODE_PRIVATE)
  val soundHelper = SoundHelper(application)

  // --- Clock Settings State ---
  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<ClockSettings> = _settings.asStateFlow()

  // --- Live Time State as Integers for FlipCard ---
  private val _currentHoursInt = MutableStateFlow(10)
  val currentHoursInt: StateFlow<Int> = _currentHoursInt.asStateFlow()

  private val _currentMinutesInt = MutableStateFlow(48)
  val currentMinutesInt: StateFlow<Int> = _currentMinutesInt.asStateFlow()

  private val _currentSecondsInt = MutableStateFlow(0)
  val currentSecondsInt: StateFlow<Int> = _currentSecondsInt.asStateFlow()

  private val _currentSecondsFormatted = MutableStateFlow("00")
  val currentSecondsFormatted: StateFlow<String> = _currentSecondsFormatted.asStateFlow()

  private val _currentAmPm = MutableStateFlow("am")
  val currentAmPm: StateFlow<String> = _currentAmPm.asStateFlow()

  // Format: 11/05/26 (MetaMuted, sentence case, untracked)
  private val _currentDateFormatted = MutableStateFlow("")
  val currentDateFormatted: StateFlow<String> = _currentDateFormatted.asStateFlow()

  // Format: Thursday (sentence case, untracked—never THURSDAY)
  private val _currentWeekdayFormatted = MutableStateFlow("")
  val currentWeekdayFormatted: StateFlow<String> = _currentWeekdayFormatted.asStateFlow()

  private val _batteryPercent = MutableStateFlow(85)
  val batteryPercent: StateFlow<Int> = _batteryPercent.asStateFlow()

  private val _isCharging = MutableStateFlow(false)
  val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()

  private val _sleepTimerSecondsLeft = MutableStateFlow(0)
  val sleepTimerSecondsLeft: StateFlow<Int> = _sleepTimerSecondsLeft.asStateFlow()

  // --- Alarms State ---
  private val _alarms = MutableStateFlow<List<AlarmItem>>(loadAlarms())
  val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()

  private val _nextAlarmText = MutableStateFlow("No upcoming alarms")
  val nextAlarmText: StateFlow<String> = _nextAlarmText.asStateFlow()

  private val _triggeringAlarm = MutableStateFlow<AlarmItem?>(null)
  val triggeringAlarm: StateFlow<AlarmItem?> = _triggeringAlarm.asStateFlow()

  // --- Timer / Focus State ---
  private val _timerActivityTag = MutableStateFlow("Study")
  val timerActivityTag: StateFlow<String> = _timerActivityTag.asStateFlow()

  private val _timerTotalSeconds = MutableStateFlow(25 * 60)
  val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

  private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
  val timerRemainingSeconds: StateFlow<Int> = _timerRemainingSeconds.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  private val _isTimerPaused = MutableStateFlow(false)
  val isTimerPaused: StateFlow<Boolean> = _isTimerPaused.asStateFlow()

  private val _focusSessions = MutableStateFlow<List<FocusSession>>(loadFocusSessions())
  val focusSessions: StateFlow<List<FocusSession>> = _focusSessions.asStateFlow()

  // --- World Clock State ---
  private val _worldCities = MutableStateFlow<List<WorldCity>>(loadWorldCities())
  val worldCities: StateFlow<List<WorldCity>> = _worldCities.asStateFlow()

  // --- Stopwatch State ---
  private val _stopwatchMillis = MutableStateFlow(0L)
  val stopwatchMillis: StateFlow<Long> = _stopwatchMillis.asStateFlow()

  private val _isStopwatchRunning = MutableStateFlow(false)
  val isStopwatchRunning: StateFlow<Boolean> = _isStopwatchRunning.asStateFlow()

  private val _stopwatchLaps = MutableStateFlow<List<StopwatchLap>>(emptyList())
  val stopwatchLaps: StateFlow<List<StopwatchLap>> = _stopwatchLaps.asStateFlow()

  private var stopwatchStartTime = 0L
  private var stopwatchAccumulated = 0L
  private var lastLapSplitTime = 0L

  private var timeTickerJob: Job? = null
  private var timerJob: Job? = null
  private var stopwatchJob: Job? = null
  private var flipGestureDetector: FlipGestureDetector? = null

  private val batteryReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level >= 0 && scale > 0) {
          _batteryPercent.value = ((level.toFloat() / scale.toFloat()) * 100).toInt()
        }
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
      }
    }
  }

  init {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    application.registerReceiver(batteryReceiver, filter)

    flipGestureDetector = FlipGestureDetector(application) {
      if (_triggeringAlarm.value != null && _triggeringAlarm.value?.flipToSnooze == true) {
        snoozeCurrentAlarm()
      }
    }
    flipGestureDetector?.start()

    startTimeTicker()
    updateNextAlarmText()
  }

  override fun onCleared() {
    super.onCleared()
    try {
      getApplication<Application>().unregisterReceiver(batteryReceiver)
    } catch (_: Exception) {}
    flipGestureDetector?.stop()
  }

  private fun startTimeTicker() {
    timeTickerJob?.cancel()
    timeTickerJob = viewModelScope.launch {
      while (isActive) {
        val cal = Calendar.getInstance()
        val s = _settings.value

        // Hours
        val rawHour = if (s.is24Hour) {
          cal.get(Calendar.HOUR_OF_DAY)
        } else {
          when (val h = cal.get(Calendar.HOUR)) {
            0 -> 12
            else -> h
          }
        }
        _currentHoursInt.value = rawHour

        // Minutes
        val minute = cal.get(Calendar.MINUTE)
        _currentMinutesInt.value = minute

        // Seconds
        val second = cal.get(Calendar.SECOND)
        _currentSecondsInt.value = second
        _currentSecondsFormatted.value = String.format(Locale.US, "%02d", second)

        // AM/PM (lowercase sentence-case per Hallmark rules)
        _currentAmPm.value = if (cal.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm"

        // Day of week (sentence case: "Thursday", untracked)
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        _currentWeekdayFormatted.value = dayFormat.format(cal.time)

        // Date: "MM/dd/yy" e.g. 11/05/26
        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
        _currentDateFormatted.value = dateFormat.format(cal.time)

        // Ticking sound
        if (s.tickingSound) {
          soundHelper.playMechanicalClick()
        }

        // Sleep timer check
        if (_sleepTimerSecondsLeft.value > 0) {
          _sleepTimerSecondsLeft.value -= 1
        }

        checkAlarmTrigger(cal)

        delay(1000L)
      }
    }
  }

  private fun checkAlarmTrigger(now: Calendar) {
    if (_triggeringAlarm.value != null) return
    val currentHour = now.get(Calendar.HOUR_OF_DAY)
    val currentMinute = now.get(Calendar.MINUTE)
    val currentSecond = now.get(Calendar.SECOND)
    val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)

    if (currentSecond == 0) {
      val match = _alarms.value.firstOrNull { alarm ->
        alarm.isEnabled &&
            alarm.hour == currentHour &&
            alarm.minute == currentMinute &&
            (alarm.days.isEmpty() || alarm.days.contains(dayOfWeek))
      }
      if (match != null) {
        triggerAlarmAlert(match)
      }
    }
  }

  fun triggerAlarmAlert(alarm: AlarmItem) {
    _triggeringAlarm.value = alarm
    if (alarm.vibrate) {
      soundHelper.vibrateAlarm()
    }
    soundHelper.playDefaultAlarmSound(
      targetVolume = alarm.volume,
      ascendingOver30Seconds = alarm.ascendingVolume
    )
  }

  fun dismissCurrentAlarm() {
    val current = _triggeringAlarm.value ?: return
    soundHelper.stopAlarmSound()
    _triggeringAlarm.value = null
    if (current.autoDelete) {
      deleteAlarm(current.id)
    } else if (current.days.isEmpty()) {
      toggleAlarm(current.id, false)
    }
  }

  fun snoozeCurrentAlarm() {
    val current = _triggeringAlarm.value ?: return
    soundHelper.stopAlarmSound()
    soundHelper.vibrateHaptic(50)
    _triggeringAlarm.value = null
    viewModelScope.launch {
      delay(current.snoozeMinutes * 60 * 1000L)
      triggerAlarmAlert(current)
    }
  }

  fun updateSettings(transform: (ClockSettings) -> ClockSettings) {
    val updated = transform(_settings.value)
    _settings.value = updated
    saveSettings(updated)
    updateNextAlarmText()
  }

  fun cycleAmbientTheme() {
    val current = com.rayhan09niloy.clockapp.model.AmbientTheme.fromName(_settings.value.ambientTheme)
    val next = current.next()
    updateSettings { it.copy(ambientTheme = next.displayName) }
    soundHelper.vibrateHaptic(15)
  }

  fun setSleepTimer(minutes: Int) {
    updateSettings { it.copy(sleepTimerMinutes = minutes) }
    _sleepTimerSecondsLeft.value = minutes * 60
  }

  fun addAlarm(alarm: AlarmItem) {
    _alarms.update { listOf(alarm) + it }
    saveAlarms(_alarms.value)
    updateNextAlarmText()
    soundHelper.vibrateHaptic()
  }

  fun updateAlarm(updated: AlarmItem) {
    _alarms.update { list ->
      list.map { if (it.id == updated.id) updated else it }
    }
    saveAlarms(_alarms.value)
    updateNextAlarmText()
  }

  fun toggleAlarm(id: String, isEnabled: Boolean) {
    _alarms.update { list ->
      list.map { if (it.id == id) it.copy(isEnabled = isEnabled) else it }
    }
    saveAlarms(_alarms.value)
    updateNextAlarmText()
    soundHelper.vibrateHaptic()
  }

  fun deleteAlarm(id: String) {
    _alarms.update { list -> list.filterNot { it.id == id } }
    saveAlarms(_alarms.value)
    updateNextAlarmText()
    soundHelper.vibrateHaptic()
  }

  private fun updateNextAlarmText() {
    val active = _alarms.value.filter { it.isEnabled }
    if (active.isEmpty()) {
      _nextAlarmText.value = "No upcoming alarms"
      return
    }
    val minNext = active.minByOrNull { it.millisUntilNextAlarm() }
    if (minNext == null || minNext.millisUntilNextAlarm() == Long.MAX_VALUE) {
      _nextAlarmText.value = "No upcoming alarms"
      return
    }
    val diffMillis = minNext.millisUntilNextAlarm()
    val hours = diffMillis / (1000 * 60 * 60)
    val minutes = (diffMillis / (1000 * 60)) % 60
    _nextAlarmText.value = when {
      hours > 0 && minutes > 0 -> "Alarm in $hours hours $minutes mins"
      hours > 0 -> "Alarm in $hours hours"
      minutes > 0 -> "Alarm in $minutes mins"
      else -> "Alarm in less than 1 min"
    }
  }

  // --- Timer / Focus Management ---
  fun setTimerDuration(seconds: Int) {
    if (!_isTimerRunning.value) {
      _timerTotalSeconds.value = seconds
      _timerRemainingSeconds.value = seconds
      _isTimerPaused.value = false
    }
  }

  fun setTimerActivityTag(tag: String) {
    _timerActivityTag.value = tag
  }

  fun startTimer() {
    if (_timerRemainingSeconds.value <= 0) return
    _isTimerRunning.value = true
    _isTimerPaused.value = false
    soundHelper.vibrateHaptic(30)

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_timerRemainingSeconds.value > 0 && isActive) {
        delay(1000L)
        _timerRemainingSeconds.value -= 1
      }
      if (_timerRemainingSeconds.value == 0) {
        completeFocusSession()
      }
      _isTimerRunning.value = false
      _isTimerPaused.value = false
    }
  }

  fun pauseTimer() {
    timerJob?.cancel()
    _isTimerRunning.value = false
    _isTimerPaused.value = true
    soundHelper.vibrateHaptic(20)
  }

  fun resetTimer() {
    timerJob?.cancel()
    _isTimerRunning.value = false
    _isTimerPaused.value = false
    _timerRemainingSeconds.value = _timerTotalSeconds.value
    soundHelper.vibrateHaptic(20)
  }

  private fun completeFocusSession() {
    val durationMin = (_timerTotalSeconds.value / 60).coerceAtLeast(1)
    val session = FocusSession(
      activityTag = _timerActivityTag.value,
      durationMinutes = durationMin
    )
    _focusSessions.update { listOf(session) + it }
    saveFocusSessions(_focusSessions.value)
    soundHelper.playChime(1.0f)
    soundHelper.vibrateAlarm()
  }

  // --- World Clock Management ---
  fun addWorldCity(city: WorldCity) {
    if (_worldCities.value.none { it.id == city.id }) {
      _worldCities.update { it + city }
      saveWorldCities(_worldCities.value)
      soundHelper.vibrateHaptic()
    }
  }

  fun removeWorldCity(id: String) {
    _worldCities.update { list -> list.filterNot { it.id == id } }
    saveWorldCities(_worldCities.value)
    soundHelper.vibrateHaptic()
  }

  // --- Stopwatch Management ---
  fun toggleStopwatch() {
    if (_isStopwatchRunning.value) {
      pauseStopwatch()
    } else {
      startStopwatch()
    }
  }

  private fun startStopwatch() {
    _isStopwatchRunning.value = true
    stopwatchStartTime = System.currentTimeMillis() - stopwatchAccumulated
    soundHelper.vibrateHaptic(30)

    stopwatchJob?.cancel()
    stopwatchJob = viewModelScope.launch {
      while (_isStopwatchRunning.value && isActive) {
        _stopwatchMillis.value = System.currentTimeMillis() - stopwatchStartTime
        delay(16L)
      }
    }
  }

  private fun pauseStopwatch() {
    _isStopwatchRunning.value = false
    stopwatchAccumulated = _stopwatchMillis.value
    stopwatchJob?.cancel()
    soundHelper.vibrateHaptic(20)
  }

  fun resetStopwatch() {
    pauseStopwatch()
    _stopwatchMillis.value = 0L
    stopwatchAccumulated = 0L
    lastLapSplitTime = 0L
    _stopwatchLaps.value = emptyList()
    soundHelper.vibrateHaptic(20)
  }

  fun recordLap() {
    if (!_isStopwatchRunning.value && _stopwatchMillis.value == 0L) return
    val total = _stopwatchMillis.value
    val lapTime = total - lastLapSplitTime
    lastLapSplitTime = total
    val newLap = StopwatchLap(
      lapNumber = _stopwatchLaps.value.size + 1,
      lapTimeMillis = lapTime,
      splitTimeMillis = total
    )
    _stopwatchLaps.update { listOf(newLap) + it }
    soundHelper.vibrateHaptic(15)
  }

  // --- Persistence Methods ---
  private fun loadSettings(): ClockSettings {
    return ClockSettings(
      flipAnimationEnabled = prefs.getBoolean("flip_anim", true),
      is24Hour = prefs.getBoolean("is_24h", false),
      leadingZero = prefs.getBoolean("leading_zero", true),
      showSplitLine = prefs.getBoolean("show_split", true),
      showSeconds = prefs.getBoolean("show_seconds", true),
      showWeekday = prefs.getBoolean("show_weekday", true),
      showDate = prefs.getBoolean("show_date", true),
      showNextAlarm = prefs.getBoolean("show_next_alarm", true),
      showBattery = prefs.getBoolean("show_battery", true),
      showWeather = prefs.getBoolean("show_weather", true),
      tickingSound = prefs.getBoolean("ticking_sound", false),
      sleepTimerMinutes = prefs.getInt("sleep_timer", 0),
      fontStyle = "SansSerif",
      ambientTheme = prefs.getString("ambient_theme", "OLED Black") ?: "OLED Black"
    )
  }

  private fun saveSettings(s: ClockSettings) {
    prefs.edit()
      .putBoolean("flip_anim", s.flipAnimationEnabled)
      .putBoolean("is_24h", s.is24Hour)
      .putBoolean("leading_zero", s.leadingZero)
      .putBoolean("show_split", s.showSplitLine)
      .putBoolean("show_seconds", s.showSeconds)
      .putBoolean("show_weekday", s.showWeekday)
      .putBoolean("show_date", s.showDate)
      .putBoolean("show_next_alarm", s.showNextAlarm)
      .putBoolean("show_battery", s.showBattery)
      .putBoolean("show_weather", s.showWeather)
      .putBoolean("ticking_sound", s.tickingSound)
      .putInt("sleep_timer", s.sleepTimerMinutes)
      .putString("font_style", "SansSerif")
      .putString("ambient_theme", s.ambientTheme)
      .apply()
  }

  private fun loadAlarms(): List<AlarmItem> {
    val raw = prefs.getString("alarms_json", null)
    if (raw.isNullOrEmpty()) {
      return listOf(
        AlarmItem(hour = 7, minute = 0, label = "Work", isEnabled = true, days = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)),
        AlarmItem(hour = 8, minute = 30, label = "Weekend Wake", isEnabled = false, days = setOf(Calendar.SATURDAY, Calendar.SUNDAY))
      )
    }
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<AlarmItem>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        val daysArr = obj.optJSONArray("days")
        val daysSet = mutableSetOf<Int>()
        if (daysArr != null) {
          for (d in 0 until daysArr.length()) {
            daysSet.add(daysArr.getInt(d))
          }
        }
        list.add(
          AlarmItem(
            id = obj.getString("id"),
            hour = obj.getInt("hour"),
            minute = obj.getInt("minute"),
            label = obj.optString("label", "Alarm"),
            isEnabled = obj.optBoolean("isEnabled", true),
            days = daysSet,
            ringtone = obj.optString("ringtone", "Classic Bell"),
            vibrate = obj.optBoolean("vibrate", true),
            volume = obj.optDouble("volume", 0.8).toFloat(),
            ascendingVolume = obj.optBoolean("ascendingVolume", true),
            autoDelete = obj.optBoolean("autoDelete", false),
            snoozeMinutes = obj.optInt("snoozeMinutes", 10),
            snoozeLimit = obj.optString("snoozeLimit", "3 times"),
            flipToSnooze = obj.optBoolean("flipToSnooze", true)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun saveAlarms(list: List<AlarmItem>) {
    val arr = JSONArray()
    for (a in list) {
      val obj = JSONObject()
      obj.put("id", a.id)
      obj.put("hour", a.hour)
      obj.put("minute", a.minute)
      obj.put("label", a.label)
      obj.put("isEnabled", a.isEnabled)
      val daysArr = JSONArray()
      a.days.forEach { daysArr.put(it) }
      obj.put("days", daysArr)
      obj.put("ringtone", a.ringtone)
      obj.put("vibrate", a.vibrate)
      obj.put("volume", a.volume.toDouble())
      obj.put("ascendingVolume", a.ascendingVolume)
      obj.put("autoDelete", a.autoDelete)
      obj.put("snoozeMinutes", a.snoozeMinutes)
      obj.put("snoozeLimit", a.snoozeLimit)
      obj.put("flipToSnooze", a.flipToSnooze)
      arr.put(obj)
    }
    prefs.edit().putString("alarms_json", arr.toString()).apply()
  }

  private fun loadFocusSessions(): List<FocusSession> {
    val raw = prefs.getString("focus_json", null) ?: return listOf(
      FocusSession(activityTag = "Deep Work", durationMinutes = 25, completedTimestamp = System.currentTimeMillis() - 7200000),
      FocusSession(activityTag = "Study", durationMinutes = 45, completedTimestamp = System.currentTimeMillis() - 3600000)
    )
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<FocusSession>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          FocusSession(
            id = obj.getString("id"),
            activityTag = obj.getString("activityTag"),
            durationMinutes = obj.getInt("durationMinutes"),
            completedTimestamp = obj.getLong("completedTimestamp")
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun saveFocusSessions(list: List<FocusSession>) {
    val arr = JSONArray()
    for (s in list.take(100)) {
      val obj = JSONObject()
      obj.put("id", s.id)
      obj.put("activityTag", s.activityTag)
      obj.put("durationMinutes", s.durationMinutes)
      obj.put("completedTimestamp", s.completedTimestamp)
      arr.put(obj)
    }
    prefs.edit().putString("focus_json", arr.toString()).apply()
  }

  private fun loadWorldCities(): List<WorldCity> {
    val raw = prefs.getString("world_cities_json", null)
    if (raw.isNullOrEmpty()) {
      return listOf(
        WorldCity("lon", "London", "United Kingdom", "Europe/London", 58, "cloudy"),
        WorldCity("tok", "Tokyo", "Japan", "Asia/Tokyo", 64, "clear"),
        WorldCity("nyc", "New York", "United States", "America/New_York", 68, "sunny"),
        WorldCity("syd", "Sydney", "Australia", "Australia/Sydney", 74, "sunny")
      )
    }
    return try {
      val arr = JSONArray(raw)
      val list = mutableListOf<WorldCity>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          WorldCity(
            id = obj.getString("id"),
            name = obj.getString("name"),
            country = obj.getString("country"),
            timeZoneId = obj.getString("timeZoneId"),
            temperatureF = obj.getInt("temperatureF"),
            weatherIcon = obj.getString("weatherIcon")
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }

  private fun saveWorldCities(list: List<WorldCity>) {
    val arr = JSONArray()
    for (c in list) {
      val obj = JSONObject()
      obj.put("id", c.id)
      obj.put("name", c.name)
      obj.put("country", c.country)
      obj.put("timeZoneId", c.timeZoneId)
      obj.put("temperatureF", c.temperatureF)
      obj.put("weatherIcon", c.weatherIcon)
      arr.put(obj)
    }
    prefs.edit().putString("world_cities_json", arr.toString()).apply()
  }
}
