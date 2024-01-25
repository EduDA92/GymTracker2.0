package com.example.gymtracker.data.repository

import com.example.gymtracker.data.dao.BarDao
import com.example.gymtracker.data.dao.PlateDao
import com.example.gymtracker.data.model.asEntity
import com.example.gymtracker.data.model.toExternalModel
import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultWeighsRepository @Inject constructor(
    private val barDao: BarDao,
    private val plateDao: PlateDao
) : WeightsRepository {

    override fun observeBars(): Flow<List<Bar>> =
        barDao.observeBars().map {
            it.toExternalModel()
        }

    override fun observePlates(): Flow<List<Plate>> =
        plateDao.observePlates().map {
            it.toExternalModel()
        }

    override suspend fun upsertBar(bar: Bar): Long =
        barDao.upsertBar(bar.asEntity())

    override suspend fun insertAllBars(barList: List<Bar>) =
        barDao.insertAllBars(barList.asEntity())

    override suspend fun upsertPlate(plate: Plate): Long =
        plateDao.upsertPlate(plate.asEntity())

    override suspend fun insertAllPlates(plates: List<Plate>) =
        plateDao.insertAllPlates(plates.asEntity())

    override suspend fun deleteBar(barId: Long) =
        barDao.deleteBar(barId)

    override suspend fun deletePlate(plateId: Long) =
        plateDao.deletePlate(plateId)

    override suspend fun updateBarIsSelected(barId: Long, isBarSelected: Boolean) =
        barDao.updateBarIsSelected(barId, isBarSelected)

    override suspend fun updatePlateIsSelected(plateId: Long, isPlateSelected: Boolean) =
        plateDao.updatePlateIsSelected(plateId, isPlateSelected)

    override suspend fun checkIfBarWeightExist(weight: Float): Boolean =
        barDao.checkIfBarWeightExist(weight)

    override suspend fun checkIfPlateWeightExist(weight: Float): Boolean =
        plateDao.checkIfPlateWeightExist(weight)
}