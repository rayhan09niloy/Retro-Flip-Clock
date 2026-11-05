package com.rayhan09niloy.clockapp

import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rayhan09niloy.clockapp.components.ClockTab
import com.rayhan09niloy.clockapp.components.RetroBottomBar
import com.rayhan09niloy.clockapp.screens.AlarmScreen
import com.rayhan09niloy.clockapp.screens.DeskClockScreen
import com.rayhan09niloy.clockapp.screens.StopwatchScreen
import com.rayhan09niloy.clockapp.screens.TimerScreen
import com.rayhan09niloy.clockapp.screens.WorldClockScreen
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.RetroFlipClockTheme
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Keep screen on for ambient desk clock mode
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    setContent {
      RetroFlipClockTheme {
        RetroClockApp()
      }
    }
  }
}

@Composable
fun RetroClockApp(
  viewModel: ClockViewModel = viewModel()
) {
  val configuration = LocalConfiguration.current
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

  // Ordered tabs: Clock, Alarm, Timer, Stopwatch, World Clock
  val tabs = remember { ClockTab.values() }
  val pagerState = rememberPagerState(
    initialPage = 0,
    pageCount = { tabs.size }
  )
  val coroutineScope = rememberCoroutineScope()

  // Landscape auto-hide state: hides after 1200ms of inactivity, shows upon interaction
  var isNavVisible by remember { mutableStateOf(!isLandscape) }
  var userInteractionTrigger by remember { mutableIntStateOf(0) }

  LaunchedEffect(isLandscape, userInteractionTrigger) {
    if (isLandscape) {
      isNavVisible = true
      delay(1200L) // Automatically hide after 1200ms of inactivity in landscape
      isNavVisible = false
    } else {
      isNavVisible = true
    }
  }

  // Any scroll/swipe on the main content area counts as interaction in landscape
  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.isScrollInProgress }.collect { isScrolling ->
      if (isScrolling && isLandscape) {
        userInteractionTrigger++
      }
    }
  }

  // Handle back button: return to CLOCK screen (page 0) if on another tab
  BackHandler(enabled = pagerState.currentPage != 0) {
    userInteractionTrigger++
    coroutineScope.launch {
      pagerState.animateScrollToPage(0)
    }
  }

  // Smoothly animate end padding in landscape so screens expand to 100% full width when nav is hidden
  val targetEndPadding = when {
    isLandscape && isNavVisible -> 92.dp
    else -> 0.dp
  }

  // Smoothly animate bottom padding in portrait so screens have clearance above bottom nav
  val targetBottomPadding = when {
    !isLandscape -> 80.dp
    else -> 0.dp
  }

  val animatedEndPadding by animateDpAsState(
    targetValue = targetEndPadding,
    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
    label = "end_nav_squeeze"
  )

  val animatedBottomPadding by animateDpAsState(
    targetValue = targetBottomPadding,
    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
    label = "bottom_nav_squeeze"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .pointerInput(isLandscape) {
        if (isLandscape) {
          // Non-blocking interaction listener for touches on screen
          awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            userInteractionTrigger++
            waitForUpOrCancellation(pass = PointerEventPass.Initial)
          }
        }
      }
  ) {
    // Main Content Area with Primary Horizontal Swipe Gestures
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(
          bottom = animatedBottomPadding,
          end = animatedEndPadding
        )
    ) {
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        userScrollEnabled = true,
        key = { page -> tabs[page].name }
      ) { page ->
        when (tabs[page]) {
          ClockTab.CLOCK -> DeskClockScreen(viewModel = viewModel)
          ClockTab.ALARM -> AlarmScreen(viewModel = viewModel)
          ClockTab.TIMER -> TimerScreen(viewModel = viewModel)
          ClockTab.STOPWATCH -> StopwatchScreen(viewModel = viewModel)
          ClockTab.WORLD -> WorldClockScreen(viewModel = viewModel)
        }
      }
    }

    // Floating Navigation Bar (Right-side vertical in Landscape, Bottom horizontal in Portrait)
    Box(
      modifier = Modifier
        .align(if (isLandscape) Alignment.CenterEnd else Alignment.BottomCenter)
        .then(if (isLandscape) Modifier.wrapContentSize() else Modifier.fillMaxWidth())
    ) {
      AnimatedVisibility(
        visible = isNavVisible || !isLandscape,
        enter = if (isLandscape) {
          slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
          ) + fadeIn(animationSpec = tween(180))
        } else {
          slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
          ) + fadeIn(animationSpec = tween(180))
        },
        exit = if (isLandscape) {
          slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
          ) + fadeOut(animationSpec = tween(180))
        } else {
          slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
          ) + fadeOut(animationSpec = tween(180))
        }
      ) {
        RetroBottomBar(
          currentTab = tabs[pagerState.currentPage],
          onTabSelected = { tab ->
            userInteractionTrigger++
            coroutineScope.launch {
              pagerState.animateScrollToPage(tab.ordinal)
            }
          },
          isLandscape = isLandscape
        )
      }
    }
  }
}
