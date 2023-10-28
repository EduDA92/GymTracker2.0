package com.example.gymtracker.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymtracker.ui.model.ExerciseAndSets

data class ExerciseWithSets(

    @Embedded
    val exercise: ExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<ExerciseSetEntity>
)

fun ExerciseWithSets.toExternalModel() = ExerciseAndSets(
    exerciseId = exercise.id,
    exerciseName = exercise.name,
    exerciseType = exercise.type,
    sets = sets.map {
        it.toExternalModel()
    }
)
