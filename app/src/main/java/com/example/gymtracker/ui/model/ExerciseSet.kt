package com.example.gymtracker.ui.model

import androidx.compose.runtime.Stable
import java.time.LocalDate

@Stable
data class ExerciseSet(
    val id: Long = 0,
    val exerciseId: Long,
    val reps: Int = 0,
    val weight: Float = 0f,
    val date: LocalDate,
    val isCompleted: Boolean = false
)
