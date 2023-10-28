package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String,
)

fun ExerciseEntity.toExternalModel() = Exercise(
    id = id,
    name = name,
    type = ExerciseType.valueOf(type)
)

fun List<ExerciseEntity>.toExternalModel() = map(ExerciseEntity::toExternalModel)

fun Exercise.asEntity() = ExerciseEntity(
    id = id,
    name = name,
    type = type.name
)
