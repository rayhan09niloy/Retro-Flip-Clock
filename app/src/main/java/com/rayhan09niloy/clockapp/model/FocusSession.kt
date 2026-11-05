package com.rayhan09niloy.clockapp.model

import java.util.UUID

data class FocusSession(
  val id: String = UUID.randomUUID().toString(),
  val activityTag: String,
  val durationMinutes: Int,
  val completedTimestamp: Long = System.currentTimeMillis()
)
