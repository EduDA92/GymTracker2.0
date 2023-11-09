package com.example.gymtracker.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.gymtracker.ui.model.WorkoutAndExercises

/* Data class that represents the info of a workout with all its exercises and for every exercise
* its corresponding sets */
data class WorkoutWithExercisesAndSets (
    @Embedded
    val workout: WorkoutEntity,

    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseCrossRef::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exerciseWithSets: List<ExerciseWithSets>
)

fun WorkoutWithExercisesAndSets.toExternalModel() = WorkoutAndExercises(
    workoutId = workout.id,
    workoutName = workout.name,
    workoutDate = workout.date,
    workoutDuration = workout.duration,
    workoutCompleted = workout.isCompleted,
    exercisesAndSets = exerciseWithSets.toExternalModel()
)
