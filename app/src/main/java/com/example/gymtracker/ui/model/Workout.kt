package com.example.gymtracker.ui.model

import java.time.LocalDate

data class Workout(
    val id: Long,
    val name: String,
    val date: LocalDate
)
