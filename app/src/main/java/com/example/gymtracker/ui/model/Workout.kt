package com.example.gymtracker.ui.model

import java.time.LocalDate

data class Workout(
    val id: Long = 0,
    val name: String = "default",
    val date: LocalDate,
    val duration: Long = 0,
    val isCompleted: Boolean = false
)
