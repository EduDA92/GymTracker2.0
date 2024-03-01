package com.example.gymtracker.ui.model

import androidx.room.ColumnInfo
import java.time.LocalDate

data class HistoryItem (
    val workoutName: String,
    val exerciseName: String,
    val exerciseSetReps: Int,
    val exerciseSetWeight: Float,
    val exerciseSetDate: LocalDate
)