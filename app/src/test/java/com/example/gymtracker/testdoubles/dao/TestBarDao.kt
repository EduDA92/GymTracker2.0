package com.example.gymtracker.testdoubles.dao

import com.example.gymtracker.data.dao.BarDao
import com.example.gymtracker.data.model.BarEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestBarDao(barList: List<BarEntity>) : BarDao {

    private val _bar = barList.toMutableList()
    private val barStream = MutableStateFlow(_bar)

    override fun observeBars(): Flow<List<BarEntity>> = barStream

    override suspend fun upsertBar(bar: BarEntity): Long {
        _bar.removeIf { it.id == bar.id }
        _bar.add(bar)
        barStream.emit(_bar)

        return bar.id
    }

    override suspend fun insertAllBars(barList: List<BarEntity>) {
        _bar.addAll(barList)
        barStream.emit(_bar)
    }

    override suspend fun deleteBar(barId: Long) {
        _bar.removeIf { it.id == barId }
        barStream.emit(_bar)
    }

    override suspend fun updateBarIsSelected(barId: Long, isBarSelected: Boolean) {
        val bar = _bar.find { it.id == barId }
        _bar.removeIf { it.id == barId }
        _bar.add(bar!!.copy(isSelected = isBarSelected))
    }

    override suspend fun checkIfBarWeightExist(weight: Float): Boolean {
        return _bar.find { it.weight == weight } != null
    }
}