package com.example.model

data class StopwatchLap(
  val lapNumber: Int,
  val lapTimeMillis: Long,
  val splitTimeMillis: Long
)
