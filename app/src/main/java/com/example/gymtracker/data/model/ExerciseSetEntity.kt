package com.example.gymtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.ExerciseSet
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate

// There is a relationship 1:N between Exercise and Set

@Entity(
    tableName = "exerciseSet",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val exerciseId: Long,
    val reps: Int,
    val weight: Float,
    val date: LocalDate,
    val isCompleted: Boolean
)

fun ExerciseSetEntity.toExternalModel() = ExerciseSet(
    id = id,
    exerciseId = exerciseId,
    reps = reps,
    weight = weight,
    date = date,
    isCompleted = isCompleted
)

fun List<ExerciseSetEntity>.toExternalModel() = map(ExerciseSetEntity::toExternalModel).toImmutableList()

fun ExerciseSet.asEntity() = ExerciseSetEntity(
    id = id,
    exerciseId = exerciseId,
    reps = reps,
    weight = weight,
    date = date,
    isCompleted = isCompleted
)
