package com.example.gymtracker.ui.model

import java.time.LocalDate

data class ExerciseSet(
    val id: Long,
    val exerciseId: Long,
    val reps: Int,
    val weight: Float,
    val date: LocalDate
)
