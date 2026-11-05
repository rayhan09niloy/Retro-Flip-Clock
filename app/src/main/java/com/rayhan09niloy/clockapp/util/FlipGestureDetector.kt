package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class FlipGestureDetector(
  context: Context,
  private val onFlipFaceDown: () -> Unit
) : SensorEventListener {
  private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
  private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private var isListening = false
  private var lastTriggerTime = 0L

  fun start() {
    if (!isListening && accelerometer != null) {
      sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
      isListening = true
    }
  }

  fun stop() {
    if (isListening) {
      sensorManager?.unregisterListener(this)
      isListening = false
    }
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
    val z = event.values[2] // Z axis: positive when screen up, negative when face down
    val now = System.currentTimeMillis()
    // When screen points down towards table, z is strongly negative (~ -9.8 m/s^2)
    if (z < -6.5f && (now - lastTriggerTime > 2000L)) {
      lastTriggerTime = now
      onFlipFaceDown()
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
