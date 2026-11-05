package com.rayhan09niloy.clockapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
  var currentTab by remember { mutableStateOf(ClockTab.CLOCK) }

  // Handle back button: return to CLOCK screen if on another tab
  BackHandler(enabled = currentTab != ClockTab.CLOCK) {
    currentTab = ClockTab.CLOCK
  }

  Scaffold(
    bottomBar = {
      RetroBottomBar(
        currentTab = currentTab,
        onTabSelected = { currentTab = it }
      )
    },
    containerColor = CanvasBlack,
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(CanvasBlack)
        .padding(bottom = innerPadding.calculateBottomPadding())
    ) {
      Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
        when (tab) {
          ClockTab.CLOCK -> DeskClockScreen(viewModel = viewModel)
          ClockTab.WORLD -> WorldClockScreen(viewModel = viewModel)
          ClockTab.STOPWATCH -> StopwatchScreen(viewModel = viewModel)
          ClockTab.TIMER -> TimerScreen(viewModel = viewModel)
          ClockTab.ALARM -> AlarmScreen(viewModel = viewModel)
        }
      }
    }
  }
}
