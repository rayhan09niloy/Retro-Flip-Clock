package com.example.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.SecondaryText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
  items: List<String>,
  selectedIndex: Int,
  onSelectedIndexChanged: (Int) -> Unit,
  modifier: Modifier = Modifier,
  fontFamily: androidx.compose.ui.text.font.FontFamily = com.example.ui.theme.PrimaryDisplayFontFamily,
  visibleItemCount: Int = 3,
  itemHeight: Dp = 44.dp,
  itemWidth: Dp = 64.dp
) {
  val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceAtLeast(0))
  val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

  LaunchedEffect(selectedIndex) {
    if (selectedIndex in items.indices && !listState.isScrollInProgress) {
      listState.animateScrollToItem(selectedIndex)
    }
  }

  val centerItemIndex by remember {
    derivedStateOf {
      val layoutInfo = listState.layoutInfo
      val visibleItems = layoutInfo.visibleItemsInfo
      if (visibleItems.isEmpty()) 0
      else {
        val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
        visibleItems.minByOrNull { Math.abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
      }
    }
  }

  LaunchedEffect(listState) {
    snapshotFlow { centerItemIndex }
      .collect { index ->
        if (index in items.indices && index != selectedIndex) {
          onSelectedIndexChanged(index)
        }
      }
  }

  Box(
    modifier = modifier
      .width(itemWidth)
      .height(itemHeight * visibleItemCount),
    contentAlignment = Alignment.Center
  ) {
    // Center selection highlight band
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(itemHeight)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF222226))
    )

    LazyColumn(
      state = listState,
      flingBehavior = flingBehavior,
      contentPadding = PaddingValues(vertical = itemHeight * ((visibleItemCount - 1) / 2)),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(items.size) { index ->
        val isSelected = index == centerItemIndex
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = items[index],
            color = if (isSelected) AmberAccent else SecondaryText,
            fontSize = if (isSelected) 26.sp else 18.sp,
            fontFamily = fontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(if (isSelected) 1f else 0.45f)
          )
        }
      }
    }
  }
}
