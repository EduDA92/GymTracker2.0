package com.example.gymtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymtracker.ui.model.Bar

@Entity(tableName = "bar")
data class BarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weight: Float,
    val isSelected: Boolean,
)

fun BarEntity.toExternalModel() = Bar(
    id = id,
    weight = weight,
    isSelected = isSelected
)

fun List<BarEntity>.toExternalModel() = map(BarEntity::toExternalModel)

fun List<Bar>.asEntity() = map(Bar::asEntity)

fun Bar.asEntity() = BarEntity(
    id = id,
    weight = weight,
    isSelected = isSelected
)


