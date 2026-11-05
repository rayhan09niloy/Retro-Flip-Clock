package com.rayhan09niloy.clockapp.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundHelper(private val context: Context) {
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    manager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  // Active alarm audio player tied directly to Android phone's default alarm sound
  private var alarmMediaPlayer: MediaPlayer? = null
  private var alarmRingtone: Ringtone? = null
  private var volumeRampJob: Job? = null

  private val clickPcm: ShortArray by lazy {
    val sampleRate = 44100
    val durationMs = 18
    val numSamples = (sampleRate * durationMs) / 1000
    val buffer = ShortArray(numSamples)
    for (i in 0 until numSamples) {
      val progress = i.toFloat() / numSamples
      val envelope = (1f - progress) * (1f - progress)
      val sample = (sin(2.0 * PI * 2400.0 * i / sampleRate) * 0.7 +
          sin(2.0 * PI * 480.0 * i / sampleRate) * 0.3) * envelope
      buffer[i] = (sample * Short.MAX_VALUE * 0.45).toInt().toShort()
    }
    buffer
  }

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
    vibrateFlip()
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
        delay(50)
        track.release()
      } catch (_: Exception) {
      }
    }
  }

  /**
   * Subtle mechanical haptic click triggered on each digit flip.
   * Uses Android's Vibration API (EFFECT_CLICK on API 29+, one-shot 12ms pulse on earlier versions).
   */
  fun vibrateFlip() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(12L, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(12L)
      }
    } catch (_: Exception) {
    }
  }

  /**
   * Plays the Android phone's default system alarm sound.
   * Starts at a very low volume (3%) and gradually increases over 30 seconds
   * to [targetVolume] to provide a gentle, non-jarring wake-up experience.
   */
  fun playDefaultAlarmSound(
    targetVolume: Float = 0.8f,
    ascendingOver30Seconds: Boolean = true
  ) {
    stopAlarmSound()
    val effectiveTargetVolume = targetVolume.coerceIn(0.1f, 1.0f)
    val initialVolume = if (ascendingOver30Seconds) 0.03f else effectiveTargetVolume

    try {
      val defaultAlarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

      if (defaultAlarmUri != null) {
        var startedWithMediaPlayer = false
        try {
          alarmMediaPlayer = MediaPlayer().apply {
            setDataSource(context, defaultAlarmUri)
            setAudioAttributes(
              AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            )
            isLooping = true
            setVolume(initialVolume, initialVolume)
            prepare()
            start()
          }
          startedWithMediaPlayer = true
        } catch (_: Exception) {
          // If MediaPlayer fails with custom URI, fallback to Ringtone
          alarmRingtone = RingtoneManager.getRingtone(context, defaultAlarmUri)?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              isLooping = true
              this.volume = initialVolume
            }
            audioAttributes = AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
            play()
          }
        }

        // Gradually increase volume over 30 seconds
        if (ascendingOver30Seconds) {
          start30SecondVolumeRamp(
            startVolume = initialVolume,
            targetVolume = effectiveTargetVolume
          )
        }
        return
      }

      playChime(initialVolume)
    } catch (_: Exception) {
      playChime(initialVolume)
    }
  }

  /**
   * Smooth 30-second volume ramp starting at low volume and increasing gently.
   */
  private fun start30SecondVolumeRamp(startVolume: Float, targetVolume: Float) {
    volumeRampJob?.cancel()
    volumeRampJob = CoroutineScope(Dispatchers.Default).launch {
      val rampDurationMs = 30_000L
      val stepIntervalMs = 250L // 4 volume updates per second for smooth progression
      val totalSteps = (rampDurationMs / stepIntervalMs).toInt()

      for (step in 1..totalSteps) {
        delay(stepIntervalMs)
        val progress = step.toFloat() / totalSteps
        // Quadratic curve for gentle beginning:
        val factor = progress * progress
        val currentVolume = (startVolume + (targetVolume - startVolume) * factor).coerceIn(0f, 1f)

        try {
          alarmMediaPlayer?.setVolume(currentVolume, currentVolume)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            alarmRingtone?.volume = currentVolume
          }
        } catch (_: Exception) {
          break
        }
      }
    }
  }

  /**
   * Stops any currently playing alarm sound and cancels the volume ramp.
   */
  fun stopAlarmSound() {
    volumeRampJob?.cancel()
    volumeRampJob = null

    try {
      alarmMediaPlayer?.let {
        if (it.isPlaying) {
          it.stop()
        }
        it.reset()
        it.release()
      }
    } catch (_: Exception) {
    } finally {
      alarmMediaPlayer = null
    }

    try {
      alarmRingtone?.let {
        if (it.isPlaying) {
          it.stop()
        }
      }
    } catch (_: Exception) {
    } finally {
      alarmRingtone = null
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
        delay(600)
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
