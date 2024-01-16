package com.example.gymtracker.testdoubles.dao

import com.example.gymtracker.data.dao.PlateDao
import com.example.gymtracker.data.model.PlateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestPlateDao(plateList: List<PlateEntity>): PlateDao {

    private val _plates = plateList.toMutableList()
    private val platesStream = MutableStateFlow(_plates)

    override fun observePlates(): Flow<List<PlateEntity>> = platesStream

    override suspend fun upsertPlate(plate: PlateEntity): Long {
        _plates.removeIf { it.id == plate.id }
        _plates.add(plate)
        platesStream.emit(_plates)

        return plate.id
    }

    override suspend fun insertAllPlates(plates: List<PlateEntity>) {
        _plates.addAll(plates)
        platesStream.emit(_plates)
    }

    override suspend fun deletePlate(plateId: Long) {
       _plates.removeIf { it.id == plateId }
        platesStream.emit(_plates)
    }

    override suspend fun updatePlateIsSelected(plateId: Long, isPlateSelected: Boolean) {
        val plate = _plates.find { it.id == plateId }
        _plates.removeIf { it.id == plateId }
        _plates.add(plate!!.copy(isSelected = isPlateSelected))

    }

    override suspend fun checkIfPlateWeightExist(weight: Float): Boolean {
        return _plates.find { it.weight == weight } != null
    }
}