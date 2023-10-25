package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.WorkoutEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class WorkoutDaoTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var workoutDao: WorkoutDao

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        workoutDao = db.workoutDao()
    }

    @Test
    fun workoutDao_UpsertAndDeleteWorkout_WorksCorrectly() = runTest {
        workoutDao.upsertWorkout(testWorkoutEntity)

        var insertedWorkouts = workoutDao.observeWorkouts().first()

        assertEquals(testWorkoutEntity, insertedWorkouts[0])

        workoutDao.deleteWorkout(testWorkoutEntity.id)

        insertedWorkouts = workoutDao.observeWorkouts().first()

        assertEquals(0, insertedWorkouts.size)
    }


    private val testWorkoutEntity = WorkoutEntity(
        id = 1,
        name = "Push",
        date = LocalDate.now()
    )

}