package com.example.gymtracker.data.repository

import com.example.gymtracker.data.model.BarEntity
import com.example.gymtracker.data.model.PlateEntity
import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate
import kotlinx.coroutines.flow.Flow

interface WeightsRepository {

    fun observeBars(): Flow<List<Bar>>

    fun observePlates(): Flow<List<Plate>>

    suspend fun upsertBar(bar: Bar): Long

    suspend fun insertAllBars(barList: List<Bar>)

    suspend fun upsertPlate(plate: Plate): Long

    suspend fun insertAllPlates(plates: List<Plate>)

    suspend fun deleteBar(barId: Long)

    suspend fun deletePlate(plateId: Long)

    suspend fun updateBarIsSelected(barId: Long, isBarSelected: Boolean)

    suspend fun updatePlateIsSelected(plateId: Long, isPlateSelected: Boolean)

    suspend fun checkIfBarWeightExist(weight: Float): Boolean

    suspend fun checkIfPlateWeightExist(weight: Float): Boolean

}