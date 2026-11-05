package com.rayhan09niloy.clockapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rayhan09niloy.clockapp.components.FlipCard
import com.rayhan09niloy.clockapp.model.WorldCity
import com.rayhan09niloy.clockapp.ui.theme.AmberAccent
import com.rayhan09niloy.clockapp.ui.theme.CardBorder
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoal
import com.rayhan09niloy.clockapp.ui.theme.DarkCharcoalElevated
import com.rayhan09niloy.clockapp.ui.theme.OledBlack
import com.rayhan09niloy.clockapp.ui.theme.PrimaryText
import com.rayhan09niloy.clockapp.ui.theme.RedAccent
import com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
import com.rayhan09niloy.clockapp.ui.theme.SecondaryText
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
  val hours by viewModel.currentHours.collectAsState()
  val minutes by viewModel.currentMinutes.collectAsState()
  val amPm by viewModel.currentAmPm.collectAsState()
  val weekday by viewModel.currentWeekdayFormatted.collectAsState()
  val dateFormatted by viewModel.currentDateFormatted.collectAsState()

  var showAddCitySheet by remember { mutableStateOf(false) }

  val currentFontFamily = when (settings.fontStyle) {
    "Anton" -> com.rayhan09niloy.clockapp.ui.theme.AntonFontFamily
    "Roboto Mono" -> com.rayhan09niloy.clockapp.ui.theme.RobotoMonoFontFamily
    else -> com.rayhan09niloy.clockapp.ui.theme.BebasNeueFontFamily
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(OledBlack)
      .statusBarsPadding()
      .padding(horizontal = 20.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 76.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "World Clock",
        color = PrimaryText,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(14.dp))

      // --- Top Widget: Mini Flip Clock (Home City) ---
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .border(1.dp, CardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = AmberAccent,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "HOME CITY (${TimeZone.getDefault().id.substringAfterLast('/')})",
                color = AmberAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              )
            }
            Text(
              text = "$weekday, $dateFormatted",
              color = SecondaryText,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Mini Flip Cards for Home City
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            FlipCard(
              value = hours,
              cardWidth = 92.dp,
              cardHeight = 62.dp,
              fontSize = 42.sp,
              fontFamily = currentFontFamily,
              showSplitLine = true,
              animateFlip = settings.flipAnimationEnabled
            )
            Text(
              text = ":",
              color = AmberAccent,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp)
            )
            FlipCard(
              value = minutes,
              cardWidth = 92.dp,
              cardHeight = 62.dp,
              fontSize = 42.sp,
              fontFamily = currentFontFamily,
              showSplitLine = true,
              animateFlip = settings.flipAnimationEnabled
            )
            if (!settings.is24Hour) {
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = amPm,
                color = AmberAccent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = currentFontFamily
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // --- Saved City Cards List ---
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(cities, key = { it.id }) { city ->
          val (cityTime, cityAmPm) = city.getCurrentTime(settings.is24Hour)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCharcoal)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Left side: Weather icon, current temp, city name, time diff
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = when (city.weatherIcon) {
                      "sunny" -> Icons.Default.WbSunny
                      "cloudy" -> Icons.Default.Cloud
                      else -> Icons.Default.NightsStay
                    },
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${city.temperatureF}°F",
                    color = SecondaryText,
                    fontSize = 13.sp,
                    fontFamily = RobotoMonoFontFamily
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "${city.getDayComparison()}, ${city.getTimeDifference()}",
                    color = SecondaryText,
                    fontSize = 12.sp
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = city.name,
                  color = PrimaryText,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = city.country,
                  color = SecondaryText,
                  fontSize = 12.sp
                )
              }

              // Right side: Large digital time with AM/PM
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = cityTime,
                  color = PrimaryText,
                  fontSize = 32.sp,
                  fontFamily = currentFontFamily,
                  fontWeight = FontWeight.Bold
                )
                if (cityAmPm.isNotEmpty()) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = cityAmPm,
                    color = AmberAccent,
                    fontSize = 15.sp,
                    fontFamily = currentFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                  )
                }

                IconButton(
                  onClick = { viewModel.removeWorldCity(city.id) },
                  modifier = Modifier
                    .size(32.dp)
                    .padding(start = 6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove City",
                    tint = SecondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // --- Bottom Right Yellow Circular Button with '+' Icon ---
    FloatingActionButton(
      onClick = { showAddCitySheet = true },
      containerColor = AmberAccent,
      contentColor = OledBlack,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 80.dp, end = 4.dp)
        .testTag("add_city_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add City", modifier = Modifier.size(28.dp))
    }

    // --- Add City Search Modal Bottom Sheet ---
    if (showAddCitySheet) {
      ModalBottomSheet(
        onDismissRequest = { showAddCitySheet = false },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkCharcoal,
        scrimColor = Color.Black.copy(alpha = 0.7f)
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

  // Global cities database
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
        text = "Add City",
        color = PrimaryText,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      IconButton(onClick = onDismiss) {
        Icon(Icons.Default.Close, contentDescription = "Close", tint = SecondaryText)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search TextField
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmberAccent) },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { searchQuery = "" }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = SecondaryText)
          }
        }
      },
      placeholder = { Text("Search city or country...") },
      singleLine = true,
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AmberAccent,
        unfocusedBorderColor = CardBorder,
        focusedTextColor = PrimaryText,
        unfocusedTextColor = PrimaryText
      ),
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      modifier = Modifier.height(340.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(filteredCities) { city ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCharcoalElevated)
            .clickable { onAddCity(city) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = city.name, color = PrimaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "${city.country} • ${city.timeZoneId}", color = SecondaryText, fontSize = 12.sp)
          }
          Icon(Icons.Default.Add, contentDescription = "Add", tint = AmberAccent)
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
