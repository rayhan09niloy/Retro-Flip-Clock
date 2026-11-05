package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Generates synthetic retro mechanical click/tick sounds and alarm chimes
 * entirely in memory via AudioTrack, with no external asset dependencies.
 */
class SoundHelper(private val context: Context) {
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    manager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  // Pre-generated mechanical click PCM buffer (16-bit mono 44.1kHz)
  private val clickPcm: ShortArray by lazy {
    val sampleRate = 44100
    val durationMs = 18
    val numSamples = (sampleRate * durationMs) / 1000
    val buffer = ShortArray(numSamples)
    for (i in 0 until numSamples) {
      val progress = i.toFloat() / numSamples
      val envelope = (1f - progress) * (1f - progress)
      // Crisp mechanical snap: combination of 2.4kHz click and low thud
      val sample = (sin(2.0 * PI * 2400.0 * i / sampleRate) * 0.7 +
          sin(2.0 * PI * 480.0 * i / sampleRate) * 0.3) * envelope
      buffer[i] = (sample * Short.MAX_VALUE * 0.45).toInt().toShort()
    }
    buffer
  }

  // Pre-generated warm chime PCM buffer
  private val chimePcm: ShortArray by lazy {
    val sampleRate = 44100
    val durationMs = 500
    val numSamples = (sampleRate * durationMs) / 1000
    val buffer = ShortArray(numSamples)
    for (i in 0 until numSamples) {
      val progress = i.toFloat() / numSamples
      val envelope = kotlin.math.exp(-3.5 * progress)
      val sample = (sin(2.0 * PI * 880.0 * i / sampleRate) * 0.6 +
          sin(2.0 * PI * 1320.0 * i / sampleRate) * 0.3 +
          sin(2.0 * PI * 1760.0 * i / sampleRate) * 0.1) * envelope
      buffer[i] = (sample * Short.MAX_VALUE * 0.6).toInt().toShort()
    }
    buffer
  }

  fun playMechanicalClick() {
    CoroutineScope(Dispatchers.Default).launch {
      try {
        val sampleRate = 44100
        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(clickPcm.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.write(clickPcm, 0, clickPcm.size)
        track.play()
        // Release after playback finishes
        kotlinx.coroutines.delay(50)
        track.release()
      } catch (_: Exception) {
      }
    }
  }

  fun playChime(volume: Float = 0.8f) {
    CoroutineScope(Dispatchers.Default).launch {
      try {
        val sampleRate = 44100
        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(chimePcm.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.setVolume(volume.coerceIn(0f, 1f))
        track.write(chimePcm, 0, chimePcm.size)
        track.play()
        kotlinx.coroutines.delay(600)
        track.release()
      } catch (_: Exception) {
      }
    }
  }

  fun vibrateHaptic(durationMs: Long = 20) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(
          VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {
    }
  }

  fun vibrateAlarm() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val timings = longArrayOf(0, 400, 200, 400)
        val amplitudes = intArrayOf(0, 255, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(longArrayOf(0, 400, 200, 400), -1)
      }
    } catch (_: Exception) {
    }
  }
}
