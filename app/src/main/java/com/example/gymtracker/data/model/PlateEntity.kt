package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.Plate

@Entity(tableName = "plate")
data class PlateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weight: Float,
    val isSelected: Boolean
)

fun PlateEntity.toExternalModel() = Plate(
    id = id,
    weight = weight,
    isSelected = isSelected
)

fun List<PlateEntity>.toExternalModel() = map(PlateEntity::toExternalModel)

fun Plate.asEntity() = PlateEntity(
    id = id,
    weight = weight,
    isSelected = isSelected
)
