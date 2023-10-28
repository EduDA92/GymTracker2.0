package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.Workout
import java.time.LocalDate

@Entity(tableName = "workout")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: LocalDate,
    val duration: Long,
    val isCompleted: Boolean
)

fun WorkoutEntity.toExternalModel() = Workout(
    id = id,
    name = name,
    date = date,
    duration = duration,
    isCompleted = isCompleted
)

fun List<WorkoutEntity>.toExernalModel() = map(WorkoutEntity::toExternalModel)

fun Workout.asEntity() = WorkoutEntity(
    id = id,
    name = name,
    date = date,
    duration = duration,
    isCompleted = isCompleted
)
