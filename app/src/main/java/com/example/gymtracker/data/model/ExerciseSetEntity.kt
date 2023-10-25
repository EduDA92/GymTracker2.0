package com.example.gymtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.ExerciseSet
import java.time.LocalDate

// There is a relationship 1:N between Exercise and Set

@Entity(
    tableName = "set",
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
    val date: LocalDate
)

fun ExerciseSetEntity.toExternalModel() = ExerciseSet(
    id = id,
    exerciseId = exerciseId,
    reps = reps,
    weight = weight,
    date = date
)
