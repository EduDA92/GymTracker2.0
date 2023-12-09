package com.example.gymtracker.ui.model

import kotlinx.collections.immutable.ImmutableList

data class ExerciseAndSets(
    val exerciseId: Long = 0,
    val exerciseName: String,
    val exerciseType: ExerciseType,
    val sets: ImmutableList<ExerciseSet>
)
