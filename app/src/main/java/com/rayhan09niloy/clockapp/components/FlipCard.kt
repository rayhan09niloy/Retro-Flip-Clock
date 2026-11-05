package com.rayhan09niloy.clockapp.components

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.FlipDigitStyle
import java.util.Locale

import androidx.compose.ui.graphics.Color

/**
 * Frameless Split-Flap Digit Group.
 * Zero cards, zero background fills, zero border outlines.
 * Renders cleanly on CanvasBlack with a 2.dp horizontal split crease.
 */
@Composable
fun FlipCard(
  value: Int,
  modifier: Modifier = Modifier,
  cardWidth: Dp = 260.dp,
  cardHeight: Dp = 160.dp,
  fontSize: TextUnit = 150.sp,
  backgroundColor: Color = CanvasBlack,
  onFlipSound: (() -> Unit)? = null
) {
  var previousValue by remember { mutableIntStateOf(value) }
  var currentValue by remember { mutableIntStateOf(value) }

  val flipAngle = remember { Animatable(0f) }
  val context = LocalContext.current

  val vibrator = remember(context) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
      val manager = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
      manager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
    }
  }

  LaunchedEffect(value) {
    if (value != currentValue) {
      previousValue = currentValue
      currentValue = value

      val animatorDurationScale = try {
        Settings.Global.getFloat(
          context.contentResolver,
          Settings.Global.ANIMATOR_DURATION_SCALE,
          1f
        )
      } catch (_: Exception) {
        1f
      }

      if (animatorDurationScale > 0f) {
        onFlipSound?.invoke()
        try {
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            vibrator?.vibrate(android.os.VibrationEffect.createPredefined(android.os.VibrationEffect.EFFECT_CLICK))
          } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator?.vibrate(android.os.VibrationEffect.createOneShot(12L, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
          } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(12L)
          }
        } catch (_: Exception) {}

        flipAngle.snapTo(0f)
        flipAngle.animateTo(
          targetValue = 180f,
          animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )
        flipAngle.snapTo(0f)
        previousValue = value
      } else {
        flipAngle.snapTo(0f)
        previousValue = value
      }
    }
  }

  val isAnimating = flipAngle.value > 0f && flipAngle.value < 180f
  val halfHeight = cardHeight / 2

  val currentFormatted = String.format(Locale.US, "%02d", currentValue)
  val previousFormatted = String.format(Locale.US, "%02d", previousValue)

  Box(
    modifier = modifier
      .width(cardWidth)
      .height(cardHeight)
      .background(backgroundColor)
  ) {
    // 1. Static Upper Half: Displays the top 50% of the NEW digit (revealed as flap drops)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(halfHeight)
        .align(Alignment.TopCenter)
        .clipToBounds()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .requiredHeight(cardHeight),
        contentAlignment = Alignment.TopCenter
      ) {
        Text(
          text = currentFormatted,
          style = FlipDigitStyle.copy(fontSize = fontSize),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 2. Static Lower Half: Displays bottom 50% of OLD digit until 90°, then NEW digit
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(halfHeight)
        .align(Alignment.BottomCenter)
        .clipToBounds()
    ) {
      val staticLowerText = if (flipAngle.value < 90f) previousFormatted else currentFormatted
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .requiredHeight(cardHeight)
          .offset(y = -halfHeight),
        contentAlignment = Alignment.TopCenter
      ) {
        Text(
          text = staticLowerText,
          style = FlipDigitStyle.copy(fontSize = fontSize),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }

    // 3. Animated 3D Flap (from 0f to 180f)
    if (isAnimating) {
      if (flipAngle.value <= 90f) {
        // Upper leaf falling forward (0° to 90°)
        val angle = flipAngle.value
        val alphaDim = 1f - (angle / 90f) * 0.28f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(halfHeight)
            .align(Alignment.TopCenter)
            .graphicsLayer {
              rotationX = -angle
              transformOrigin = TransformOrigin(0.5f, 1f)
              cameraDistance = 16f * density
              alpha = alphaDim
            }
            .clipToBounds()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .requiredHeight(cardHeight),
            contentAlignment = Alignment.TopCenter
          ) {
            Text(
              text = previousFormatted,
              style = FlipDigitStyle.copy(fontSize = fontSize),
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      } else {
        // Lower leaf swinging into position (90° to 180°)
        val angle = flipAngle.value
        val alphaDim = 1f - ((180f - angle) / 90f) * 0.28f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(halfHeight)
            .align(Alignment.BottomCenter)
            .graphicsLayer {
              rotationX = 180f - angle
              transformOrigin = TransformOrigin(0.5f, 0f)
              cameraDistance = 16f * density
              alpha = alphaDim
            }
            .clipToBounds()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .requiredHeight(cardHeight)
              .offset(y = -halfHeight),
            contentAlignment = Alignment.TopCenter
          ) {
            Text(
              text = currentFormatted,
              style = FlipDigitStyle.copy(fontSize = fontSize),
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // 4. Physical Split Crease: 2.dp horizontal cut line across the exact center
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(2.dp)
        .align(Alignment.Center)
        .background(backgroundColor)
    )
  }
}
