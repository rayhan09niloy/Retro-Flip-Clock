package com.example

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
import com.example.components.ClockTab
import com.example.components.RetroBottomBar
import com.example.screens.AlarmScreen
import com.example.screens.DeskClockScreen
import com.example.screens.StopwatchScreen
import com.example.screens.TimerScreen
import com.example.screens.WorldClockScreen
import com.example.ui.theme.OledBlack
import com.example.ui.theme.RetroFlipClockTheme
import com.example.viewmodel.ClockViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Keep screen on for desk ambient clock mode
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
    containerColor = OledBlack,
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(OledBlack)
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
