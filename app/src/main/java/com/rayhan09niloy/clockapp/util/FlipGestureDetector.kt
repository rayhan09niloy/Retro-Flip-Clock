package com.rayhan09niloy.clockapp.util

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
  private var isFaceDown = false

  fun start() {
    accelerometer?.let {
      sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
    }
  }

  fun stop() {
    sensorManager?.unregisterListener(this)
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
      val z = event.values[2]
      // When phone is placed flat face-down, z-axis acceleration is approx -9.8 m/s^2
      val faceDownNow = z < -7.0f
      if (faceDownNow && !isFaceDown) {
        isFaceDown = true
        onFlipFaceDown()
      } else if (!faceDownNow) {
        isFaceDown = false
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
