package com.example.gymtracker.ui.model

data class ExerciseAndSets(
    val exerciseId: Long = 0,
    val exerciseName: String,
    val exerciseType: String,
    val sets: List<ExerciseSet>
)
