package com.example.gymtracker.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import kotlinx.collections.immutable.toImmutableList

data class ExerciseWithSets(

    @Embedded
    val exercise: ExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseId"
    )
    val sets: List<ExerciseSetEntity>
)

fun List<ExerciseWithSets>.toExternalModel() = map(ExerciseWithSets::toExternalModel).toImmutableList()

fun ExerciseWithSets.toExternalModel() = ExerciseAndSets(
    exerciseId = exercise.id,
    exerciseName = exercise.name,
    exerciseType = ExerciseType.valueOf(exercise.type),
    sets = sets.toExternalModel()
)
