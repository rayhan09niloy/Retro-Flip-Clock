package com.rayhan09niloy.clockapp.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.ui.theme.BebasNeueFontFamily
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.PrimaryText
import kotlinx.coroutines.launch

/**
 * High-performance realistic mechanical split-flap card.
 * Features:
 * - Huge edge-to-edge typography where numbers take all available space
 * - True mechanical split-flap physics (top leaf drops forward, lower leaf flexes on impact)
 * - Zero lag / 60+ FPS hardware accelerated 3D graphicsLayer
 */
@Composable
fun FlipCard(
  value: String,
  modifier: Modifier = Modifier,
  cardWidth: Dp = 320.dp,
  cardHeight: Dp = 190.dp,
  fontSize: TextUnit = 175.sp,
  fontFamily: FontFamily = BebasNeueFontFamily,
  showSplitLine: Boolean = true,
  animateFlip: Boolean = true,
  onFlipSound: (() -> Unit)? = null
) {
  var previousValue by remember { mutableStateOf(value) }
  var currentValue by remember { mutableStateOf(value) }

  // 0f -> 180f represents the falling leaf rotating from top to bottom
  val flipAngle = remember { Animatable(0f) }
  // Realistic physical impact flex of the lower half leaf when flap lands
  val lowerBounceAngle = remember { Animatable(0f) }

  LaunchedEffect(value) {
    if (value != currentValue) {
      if (animateFlip) {
        previousValue = currentValue
        currentValue = value
        onFlipSound?.invoke()

        flipAngle.snapTo(0f)
        lowerBounceAngle.snapTo(0f)

        launch {
          // Accelerate downwards by gravity
          flipAngle.animateTo(
            targetValue = 180f,
            animationSpec = tween(durationMillis = 240, easing = CubicBezierEasing(0.35f, 0f, 0.25f, 1f))
          )
          flipAngle.snapTo(0f)
          previousValue = value
        }

        // Trigger mechanical impact flex on the lower leaf right as flap strikes
        launch {
          kotlinx.coroutines.delay(160)
          lowerBounceAngle.animateTo(
            targetValue = 4.5f,
            animationSpec = tween(durationMillis = 50, easing = FastOutLinearInEasing)
          )
          lowerBounceAngle.animateTo(
            targetValue = 0f,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = 800f)
          )
        }
      } else {
        currentValue = value
        previousValue = value
      }
    }
  }

  val isAnimating = flipAngle.value > 0f && flipAngle.value < 180f
  val halfHeight = cardHeight / 2

  Box(
    modifier = modifier
      .width(cardWidth)
      .height(cardHeight)
      .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black)
      .clip(RoundedCornerShape(16.dp))
      .background(DarkCharcoal)
      .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
  ) {
    if (!isAnimating) {
      // 1. Static Resting State: perfectly continuous card where numbers take full space
      HalfLeafStatic(
        value = currentValue,
        isTop = true,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        fontSize = fontSize,
        fontFamily = fontFamily,
        modifier = Modifier.align(Alignment.TopCenter)
      )
      HalfLeafStatic(
        value = currentValue,
        isTop = false,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        fontSize = fontSize,
        fontFamily = fontFamily,
        modifier = Modifier.align(Alignment.BottomCenter)
      )
    } else {
      // 2. Realistic Mechanical Flipping Physics
      // Upper stationary leaf: reveals the NEW number as flap falls away
      HalfLeafStatic(
        value = currentValue,
        isTop = true,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        fontSize = fontSize,
        fontFamily = fontFamily,
        modifier = Modifier.align(Alignment.TopCenter)
      )

      // Lower stationary leaf: displays OLD number until falling flap covers it,
      // with a slight tactile flex/bounce as the flap strikes it!
      HalfLeafStatic(
        value = previousValue,
        isTop = false,
        cardWidth = cardWidth,
        cardHeight = cardHeight,
        fontSize = fontSize,
        fontFamily = fontFamily,
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .graphicsLayer {
            rotationX = lowerBounceAngle.value
            cameraDistance = 14f * density
            transformOrigin = TransformOrigin(0.5f, 0f)
          }
      )

      // The Falling Flap
      if (flipAngle.value <= 90f) {
        // Upper leaf falling down from 0° to 90° (pivoting on bottom seam)
        val shadowAlpha = (flipAngle.value / 90f) * 0.35f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(halfHeight)
            .align(Alignment.TopCenter)
            .graphicsLayer {
              rotationX = -flipAngle.value
              cameraDistance = 14f * density
              transformOrigin = TransformOrigin(0.5f, 1f)
            }
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clipToBounds()
        ) {
          HalfLeafContent(
            value = previousValue,
            isTop = true,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            fontSize = fontSize,
            fontFamily = fontFamily
          )
          // Shading as it tilts away from ambient light
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = shadowAlpha))
          )
        }
      } else {
        // Lower leaf swinging down from 90° to 180° onto the bottom (pivoting on top seam)
        val highlightAlpha = ((180f - flipAngle.value) / 90f) * 0.25f
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(halfHeight)
            .align(Alignment.BottomCenter)
            .graphicsLayer {
              rotationX = 180f - flipAngle.value
              cameraDistance = 14f * density
              transformOrigin = TransformOrigin(0.5f, 0f)
            }
            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .clipToBounds()
        ) {
          HalfLeafContent(
            value = currentValue,
            isTop = false,
            cardWidth = cardWidth,
            cardHeight = cardHeight,
            fontSize = fontSize,
            fontFamily = fontFamily
          )
          // Light sheen as it sweeps down to settle
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = highlightAlpha))
          )
        }
      }
    }

    // Horizontal Seam Divider across the exact middle
    if (showSplitLine) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.Center)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0x66000000))
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color(0xFF060608))
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0x2AFFFFFF))
        )
      }
    }

    // Left and Right mechanical hinge brackets
    HingeBracket(isLeft = true)
    HingeBracket(isLeft = false)
  }
}

/**
 * Stationary half leaf component.
 */
@Composable
private fun HalfLeafStatic(
  value: String,
  isTop: Boolean,
  cardWidth: Dp,
  cardHeight: Dp,
  fontSize: TextUnit,
  fontFamily: FontFamily,
  modifier: Modifier = Modifier
) {
  val halfHeight = cardHeight / 2
  val shape = if (isTop) {
    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
  } else {
    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
  }

  Box(
    modifier = modifier
      .width(cardWidth)
      .height(halfHeight)
      .clip(shape)
      .clipToBounds()
  ) {
    HalfLeafContent(
      value = value,
      isTop = isTop,
      cardWidth = cardWidth,
      cardHeight = cardHeight,
      fontSize = fontSize,
      fontFamily = fontFamily
    )
  }
}

/**
 * Renders the top or bottom half of the number with edge-to-edge typography.
 */
@Composable
private fun HalfLeafContent(
  value: String,
  isTop: Boolean,
  cardWidth: Dp,
  cardHeight: Dp,
  fontSize: TextUnit,
  fontFamily: FontFamily
) {
  val halfHeight = cardHeight / 2
  val bgGradient = if (isTop) {
    Brush.verticalGradient(listOf(Color(0xFF1E1E22), Color(0xFF151518)))
  } else {
    Brush.verticalGradient(listOf(Color(0xFF131315), Color(0xFF1B1B1E)))
  }

  Box(
    modifier = Modifier
      .width(cardWidth)
      .height(halfHeight)
      .background(bgGradient)
  ) {
    // Renders the full text shifted so that only the top or bottom half shows
    Box(
      modifier = Modifier
        .width(cardWidth)
        .height(cardHeight)
        .offset(y = if (isTop) 0.dp else -halfHeight),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = value,
        color = PrimaryText,
        fontSize = fontSize,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        style = TextStyle(
          platformStyle = PlatformTextStyle(includeFontPadding = false),
          letterSpacing = 1.sp
        ),
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun BoxScope.HingeBracket(isLeft: Boolean) {
  val align = if (isLeft) Alignment.CenterStart else Alignment.CenterEnd
  val offsetX = if (isLeft) (-1).dp else 1.dp

  Box(
    modifier = Modifier
      .align(align)
      .offset(x = offsetX)
      .width(8.dp)
      .height(22.dp)
      .clip(RoundedCornerShape(3.dp))
      .background(Color(0xFF09090C))
      .border(1.dp, Color(0xFF32323A), RoundedCornerShape(3.dp))
  )
}
