package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.BarEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BarDaoTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var barDao: BarDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        barDao = db.barDao()
    }

    @Test
    fun barDao_upsertAndDeletePlate_worksAsExpected() = runTest {

        barDao.upsertBar(barEntity)

        var bars = barDao.observeBars().first()

        assertEquals(barEntity, bars[0])

        barDao.deleteBar(1)

        bars = barDao.observeBars().first()

        assertEquals(0, bars.size)

    }

    @Test
    fun barDao_insertAllBars_worksAsExpected() = runTest {

        val barList = listOf(barEntity, barEntity.copy(id = 2), barEntity.copy(id = 3))

        barDao.insertAllBars(barList)

        val bars = barDao.observeBars().first()

        assertEquals(barList, bars)

    }

    @Test
    fun barDao_updateBarIsSelected_worksAsExpected() = runTest {

        barDao.upsertBar(barEntity)

        barDao.updateBarIsSelected(1, true)

        var bars = barDao.observeBars().first()

        assertEquals(barEntity.copy(isSelected = true), bars[0])

        barDao.updateBarIsSelected(1, false)

        bars = barDao.observeBars().first()

        assertEquals(barEntity, bars[0])

    }

    @Test
    fun barDao_checkIfBarAlreadyExist_returnsCorrectValue() = runTest {

        barDao.upsertBar(barEntity)

        var exist = barDao.checkIfBarWeightExist(22f)

        assertEquals(exist, false)

        exist = barDao.checkIfBarWeightExist(20f)

        assertEquals(exist, true)


    }

    private val barEntity = BarEntity(
        id = 1,
        weight = 20f,
        isSelected = false
    )

}