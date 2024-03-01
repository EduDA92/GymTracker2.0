package com.example.gymtracker.data.model

import androidx.room.ColumnInfo
import com.example.gymtracker.ui.model.HistoryItem
import java.time.LocalDate

data class ExerciseSetHistoryItem(
    @ColumnInfo("WorkoutName")
    val workoutName: String,
    @ColumnInfo("name")
    val exerciseName: String,
    @ColumnInfo("reps")
    val exerciseSetReps: Int,
    @ColumnInfo("weight")
    val exerciseSetWeight: Float,
    @ColumnInfo("date")
    val exerciseSetDate: LocalDate
)

fun ExerciseSetHistoryItem.toExternalModel() = HistoryItem(
    workoutName = workoutName,
    exerciseName = exerciseName,
    exerciseSetReps = exerciseSetReps,
    exerciseSetWeight = exerciseSetWeight,
    exerciseSetDate = exerciseSetDate
)

fun List<ExerciseSetHistoryItem>.toExternalModel() = map(ExerciseSetHistoryItem::toExternalModel)