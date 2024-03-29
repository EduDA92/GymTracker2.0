package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.ExerciseEntity
import com.example.gymtracker.data.model.ExerciseSetEntity
import com.example.gymtracker.data.model.ExerciseSetHistoryItem
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.ui.model.ExerciseType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ExerciseSetDaoTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseSetDao: ExerciseSetDao
    private lateinit var workoutDao: WorkoutDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        exerciseDao = db.exerciseDao()
        exerciseSetDao = db.exerciseSetDao()
        workoutDao = db.workoutDao()
    }

    @Test
    fun exerciseSetDao_UpsertAndDeleteExerciseSet_WorksCorrectly() = runTest {

        /*Insert Exercise otherwise because of the relation between exercise and set if there is no
        * exercise in the db there will be foreign key error*/
        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)

        var exerciseSets = exerciseSetDao.observeExerciseSets().first()

        assertEquals(testExerciseSet, exerciseSets[0])

        exerciseSetDao.deleteExerciseSet(testExerciseSet.id)

        exerciseSets = exerciseSetDao.observeExerciseSets().first()

        assertEquals(0, exerciseSets.size)

    }

    @Test
    fun exerciseSetDao_UpdateIsComplete_UpdatesCorrectly() = runTest {
        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)

        exerciseSetDao.updateCompleteExerciseSet(testExerciseSet.id, true)

        val exerciseSets = exerciseSetDao.observeExerciseSets().first()

        assertEquals(testExerciseSet.copy(isCompleted = true), exerciseSets[0])
    }

    @Test
    fun exerciseSetDao_updateExerciseSetData_updatesAccordingly() = runTest {

        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)

        exerciseSetDao.updateExerciseSetData(3, 10, 10f)

        val exerciseSet = exerciseSetDao.observeExerciseSets().first()

        assertEquals(testExerciseSet.copy(reps = 10, weight = 10f), exerciseSet[0])


    }

    @Test
    fun exerciseSetDao_getExerciseSetIds_returnsCorrectData() = runTest {

        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)
        exerciseSetDao.upsertExerciseSet(testExerciseSet2)
        exerciseSetDao.upsertExerciseSet(testExerciseSet3)

        val listOfIds = exerciseSetDao.getExerciseSetIdList(1, LocalDate.now())

        assertEquals(listOf(3L,4L), listOfIds)

    }

    @Test
    fun exerciseSetDao_deleteExerciseSets_deletesCorrectly() = runTest{

        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)
        exerciseSetDao.upsertExerciseSet(testExerciseSet2)
        exerciseSetDao.upsertExerciseSet(testExerciseSet3)

        exerciseSetDao.deleteExerciseSets(listOf(3L,4L))

        val listOfIds = exerciseSetDao.getExerciseSetIdList(1, LocalDate.now())

        assertEquals(emptyList<Long>(), listOfIds)

    }

    @Test
    fun exerciseSetDao_getExerciseSetHistory_returnsCorrectData() = runTest {

        workoutDao.upsertWorkout(workoutEntity1)
        workoutDao.upsertWorkout(workoutEntity2)

        exerciseDao.upsertExercise(testExerciseEntity)

        workoutDao.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = 1, exerciseId = 1))
        workoutDao.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(workoutId = 2, exerciseId = 1))

        exerciseSetDao.upsertExerciseSet(testExerciseSet)
        exerciseSetDao.upsertExerciseSet(testExerciseSet2)
        exerciseSetDao.upsertExerciseSet(testExerciseSet3)

        val daoListOfHistoryItems = exerciseSetDao.getExerciseSetHistory(1).first()

        assertEquals(listOfHistoryItems, daoListOfHistoryItems)

    }

    @Test
    fun exerciseSetDao_deleteExerciseFromData_deletesSelectedDateTests() = runTest {

        exerciseDao.upsertExercise(testExerciseEntity)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)
        exerciseSetDao.upsertExerciseSet(testExerciseSet2)
        exerciseSetDao.upsertExerciseSet(testExerciseSet3)

        exerciseSetDao.deleteExerciseSetFromDate(LocalDate.now())

        val exerciseSetList = exerciseSetDao.observeExerciseSets().first()

        assertEquals(listOf(testExerciseSet3), exerciseSetList)
    }

    private val testExerciseEntity = ExerciseEntity(
        id = 1,
        name = "Deadlift",
        type = ExerciseType.Legs.name
    )

    private val workoutEntity1 = WorkoutEntity(
        id = 1,
        name = "Push",
        date = LocalDate.now(),
        duration = 0L,
        isCompleted = true
    )

    private val workoutEntity2 = WorkoutEntity(
        id = 2,
        name = "Pull",
        date = LocalDate.now().plusDays(1),
        duration = 0L,
        isCompleted = true
    )

    private val testExerciseSet = ExerciseSetEntity(
        id = 3,
        exerciseId = 1,
        reps = 12,
        weight = 100f,
        date = LocalDate.now(),
        isCompleted = false
    )

    private val testExerciseSet2 = ExerciseSetEntity(
        id = 4,
        exerciseId = 1,
        reps = 12,
        weight = 100f,
        date = LocalDate.now(),
        isCompleted = false
    )

    private val testExerciseSet3 = ExerciseSetEntity(
        id = 5,
        exerciseId = 1,
        reps = 12,
        weight = 100f,
        date = LocalDate.now().plusDays(1),
        isCompleted = false
    )

    private val listOfHistoryItems = listOf<ExerciseSetHistoryItem>(
        ExerciseSetHistoryItem(
            workoutName = "Push",
            exerciseName = "Deadlift",
            exerciseSetReps = 12,
            exerciseSetWeight = 100f,
            exerciseSetDate = LocalDate.now()
        ),
        ExerciseSetHistoryItem(
            workoutName = "Push",
            exerciseName = "Deadlift",
            exerciseSetReps = 12,
            exerciseSetWeight = 100f,
            exerciseSetDate = LocalDate.now()
        ),
        ExerciseSetHistoryItem(
            workoutName = "Pull",
            exerciseName = "Deadlift",
            exerciseSetReps = 12,
            exerciseSetWeight = 100f,
            exerciseSetDate = LocalDate.now().plusDays(1)
        )
    )
}