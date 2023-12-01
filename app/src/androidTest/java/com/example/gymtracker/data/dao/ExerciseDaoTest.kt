package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.ExerciseEntity
import com.example.gymtracker.ui.model.ExerciseType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ExerciseDaoTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var exerciseDao: ExerciseDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        exerciseDao = db.exerciseDao()
    }

    @Test
    fun exerciseDao_UpsertAndDeleteExercise_WorksCorrectly() = runTest {
        exerciseDao.upsertExercise(testExerciseEntity)

        var exercises = exerciseDao.observeExercises().first()

        assertEquals(testExerciseEntity, exercises[0])

        exerciseDao.deleteExercise(testExerciseEntity.id)

        exercises = exerciseDao.observeExercises().first()

        assertEquals(0, exercises.size)

    }

    private val testExerciseEntity = ExerciseEntity(
        id = 1,
        name = "Deadlift",
        type = ExerciseType.Legs.name
    )
}