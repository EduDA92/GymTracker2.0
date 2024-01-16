package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.PlateEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class PlateDaoTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var plateDao: PlateDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        plateDao = db.plateDao()
    }

    @Test
    fun plateDao_UpsertAndDeletePlate_worksAsExpected() = runTest {

        // insert plate and check
        plateDao.upsertPlate(plateEntity)

        var plates = plateDao.observePlates().first()

        assertEquals(plateEntity, plates[0])

        // delete the plate
        plateDao.deletePlate(1)

        plates = plateDao.observePlates().first()

        assertEquals(0, plates.size)

    }

    @Test
    fun plateDao_insertAllPlates_worksAsExpected() = runTest {

        val plateList = listOf(plateEntity, plateEntity.copy(id = 2), plateEntity.copy(id = 3))

        plateDao.insertAllPlates(plateList)

        var plates = plateDao.observePlates().first()

        assertEquals(plateList, plates)

    }

    @Test
    fun plateDao_updatePlateIsSelected_worksAsExpected() = runTest {

        plateDao.upsertPlate(plateEntity)

        plateDao.updatePlateIsSelected(1, true)

        var plates = plateDao.observePlates().first()

        assertEquals(plateEntity.copy(isSelected = true), plates[0])

        plateDao.updatePlateIsSelected(1, false)

        plates = plateDao.observePlates().first()

        assertEquals(plateEntity, plates[0])

    }

    @Test
    fun plateDao_checkIfPlateWeightAlreadyExist_returnsCorrectValue() = runTest {

        plateDao.upsertPlate(plateEntity)

        var exist = plateDao.checkIfPlateWeightExist(22f)

        assertEquals(exist, false)

        exist = plateDao.checkIfPlateWeightExist(20f)

        assertEquals(exist, true)

    }

    private val plateEntity = PlateEntity(
        id = 1,
        weight = 20f,
        isSelected = false
    )
}