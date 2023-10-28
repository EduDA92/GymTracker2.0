package com.example.gymtracker.ui.model

import java.time.LocalDate

data class WorkoutAndExercises (
    val workoutId: Long = 0,
    val workoutName: String,
    val workoutDate: LocalDate,
    val workoutDuration: Long,
    val workoutCompleted: Boolean,
    val exercisesAndSets: List<ExerciseAndSets>
)
