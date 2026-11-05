package com.rayhan09niloy.clockapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.model.WorldCity
import com.rayhan09niloy.clockapp.ui.theme.CanvasBlack
import com.rayhan09niloy.clockapp.ui.theme.FlipDigitStyle
import com.rayhan09niloy.clockapp.ui.theme.FlapBone
import com.rayhan09niloy.clockapp.ui.theme.HairlineDivider
import com.rayhan09niloy.clockapp.ui.theme.MetaLabelStyle
import com.rayhan09niloy.clockapp.ui.theme.MetaMuted
import com.rayhan09niloy.clockapp.ui.theme.SignalAmber
import com.rayhan09niloy.clockapp.viewmodel.ClockViewModel
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorldClockScreen(
  viewModel: ClockViewModel,
  modifier: Modifier = Modifier
) {
  val cities by viewModel.worldCities.collectAsState()
  val settings by viewModel.settings.collectAsState()
  val hours by viewModel.currentHoursInt.collectAsState()
  val minutes by viewModel.currentMinutesInt.collectAsState()
  val amPm by viewModel.currentAmPm.collectAsState()
  val weekday by viewModel.currentWeekdayFormatted.collectAsState()
  val dateFormatted by viewModel.currentDateFormatted.collectAsState()

  var showAddCitySheet by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CanvasBlack)
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 64.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "World clock",
        color = FlapBone,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(16.dp))

      // --- Top Section: Home city without house emoji/icon (Floor #11 zero cards) ---
      Column(
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Untracked sentence-case label (Floor #9)
          Text(
            text = "Home city (${TimeZone.getDefault().id.substringAfterLast('/').replace('_', ' ')})",
            style = MetaLabelStyle,
            color = MetaMuted
          )
          Text(
            text = "$weekday, $dateFormatted",
            style = MetaLabelStyle,
            color = MetaMuted,
            fontSize = 12.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mini frameless flip cards for Home City
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          FlipCard(
            value = hours,
            cardWidth = 84.dp,
            cardHeight = 56.dp,
            fontSize = 42.sp
          )
          Text(
            text = ":",
            color = FlapBone,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
          )
          FlipCard(
            value = minutes,
            cardWidth = 84.dp,
            cardHeight = 56.dp,
            fontSize = 42.sp
          )
          if (!settings.is24Hour) {
            Text(
              text = " $amPm",
              style = MetaLabelStyle,
              modifier = Modifier.padding(start = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Hairline divider separating Home city from Saved cities
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(1.dp)
          .background(HairlineDivider)
      )

      // --- Saved Cities List (Separated ONLY by 1.dp HairlineDivider directly on CanvasBlack) ---
      LazyColumn(
        modifier = Modifier.weight(1f)
      ) {
        items(cities, key = { it.id }) { city ->
          val (cityTime, cityAmPm) = city.getCurrentTime(settings.is24Hour)

          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = city.name,
                  color = FlapBone,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${city.getDayComparison()}, ${city.getTimeDifference()}",
                  style = MetaLabelStyle,
                  color = MetaMuted,
                  fontSize = 12.sp
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.Bottom) {
                  Text(
                    text = cityTime,
                    style = FlipDigitStyle.copy(fontSize = 28.sp)
                  )
                  if (cityAmPm.isNotEmpty()) {
                    Text(
                      text = " $cityAmPm",
                      style = MetaLabelStyle,
                      fontSize = 12.sp,
                      modifier = Modifier.padding(bottom = 2.dp)
                    )
                  }
                }

                IconButton(
                  onClick = { viewModel.removeWorldCity(city.id) },
                  modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = MetaMuted,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }

            // 1.dp hairline divider
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HairlineDivider)
            )
          }
        }
      }
    }

    // Bottom Right Add Button
    FloatingActionButton(
      onClick = { showAddCitySheet = true },
      containerColor = SignalAmber,
      contentColor = CanvasBlack,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 72.dp, end = 4.dp)
        .testTag("add_city_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add city", modifier = Modifier.size(24.dp))
    }

    // Add City Modal Bottom Sheet
    if (showAddCitySheet) {
      ModalBottomSheet(
        onDismissRequest = { showAddCitySheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = CanvasBlack,
        scrimColor = CanvasBlack.copy(alpha = 0.8f)
      ) {
        AddCitySheetContent(
          onAddCity = { city ->
            viewModel.addWorldCity(city)
            showAddCitySheet = false
          },
          onDismiss = { showAddCitySheet = false }
        )
      }
    }
  }
}

@Composable
private fun AddCitySheetContent(
  onAddCity: (WorldCity) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  val allCities = remember {
    listOf(
      WorldCity("lon", "London", "United Kingdom", "Europe/London", 58, "cloudy"),
      WorldCity("tok", "Tokyo", "Japan", "Asia/Tokyo", 64, "clear"),
      WorldCity("nyc", "New York", "United States", "America/New_York", 68, "sunny"),
      WorldCity("syd", "Sydney", "Australia", "Australia/Sydney", 74, "sunny"),
      WorldCity("par", "Paris", "France", "Europe/Paris", 61, "cloudy"),
      WorldCity("ber", "Berlin", "Germany", "Europe/Berlin", 59, "cloudy"),
      WorldCity("dxb", "Dubai", "United Arab Emirates", "Asia/Dubai", 88, "sunny"),
      WorldCity("sin", "Singapore", "Singapore", "Asia/Singapore", 84, "rainy"),
      WorldCity("sfo", "San Francisco", "United States", "America/Los_Angeles", 65, "clear"),
      WorldCity("tor", "Toronto", "Canada", "America/Toronto", 55, "cloudy"),
      WorldCity("hkg", "Hong Kong", "China", "Asia/Hong_Kong", 78, "sunny"),
      WorldCity("seo", "Seoul", "South Korea", "Asia/Seoul", 62, "clear"),
      WorldCity("rom", "Rome", "Italy", "Europe/Rome", 69, "sunny"),
      WorldCity("cai", "Cairo", "Egypt", "Africa/Cairo", 82, "sunny"),
      WorldCity("bom", "Mumbai", "India", "Asia/Kolkata", 86, "sunny"),
      WorldCity("mex", "Mexico City", "Mexico", "America/Mexico_City", 72, "cloudy"),
      WorldCity("sao", "São Paulo", "Brazil", "America/Sao_Paulo", 75, "rainy"),
      WorldCity("ams", "Amsterdam", "Netherlands", "Europe/Amsterdam", 57, "cloudy"),
      WorldCity("bkk", "Bangkok", "Thailand", "Asia/Bangkok", 89, "sunny"),
      WorldCity("akl", "Auckland", "New Zealand", "Pacific/Auckland", 63, "cloudy")
    )
  }

  val filteredCities = remember(searchQuery) {
    if (searchQuery.isBlank()) allCities
    else allCities.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
          it.country.contains(searchQuery, ignoreCase = true)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Add city",
        color = FlapBone,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = MetaMuted)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MetaMuted) },
      placeholder = { Text("Search city or country...", color = MetaMuted) },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SignalAmber,
        unfocusedBorderColor = HairlineDivider,
        focusedTextColor = FlapBone,
        unfocusedTextColor = FlapBone,
        focusedContainerColor = CanvasBlack,
        unfocusedContainerColor = CanvasBlack
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      modifier = Modifier.height(320.dp)
    ) {
      items(filteredCities) { city ->
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onAddCity(city) }
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = city.name, color = FlapBone, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
              Text(text = "${city.country} • ${city.timeZoneId}", color = MetaMuted, fontSize = 12.sp)
            }
            Icon(Icons.Default.Add, contentDescription = "Add", tint = SignalAmber)
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(1.dp)
              .background(HairlineDivider)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
