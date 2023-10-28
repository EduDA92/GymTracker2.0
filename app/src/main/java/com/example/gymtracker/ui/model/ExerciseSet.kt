package com.example.gymtracker.ui.model

import java.time.LocalDate

data class ExerciseSet(
    val id: Long = 0,
    val exerciseId: Long,
    val reps: Int = 0,
    val weight: Float = 0f,
    val date: LocalDate,
    val isCompleted: Boolean = false
)
