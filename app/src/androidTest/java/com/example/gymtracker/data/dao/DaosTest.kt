package com.example.gymtracker.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.gymtracker.data.GymTrackerDatabase
import com.example.gymtracker.data.model.ExerciseEntity
import com.example.gymtracker.data.model.ExerciseSetEntity
import com.example.gymtracker.data.model.ExerciseWithSets
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.model.WorkoutWithExercisesAndSets
import com.example.gymtracker.ui.model.ExerciseType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class DaosTest {

    private lateinit var db: GymTrackerDatabase
    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseSetDao: ExerciseSetDao

    @Before
    fun createDb(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            GymTrackerDatabase::class.java
        ).build()
        workoutDao = db.workoutDao()
        exerciseDao = db.exerciseDao()
        exerciseSetDao = db.exerciseSetDao()
    }

    @Test
    fun workoutDao_UpsertAndDeleteWorkout_WorksCorrectly() = runTest {
        workoutDao.upsertWorkout(testWorkoutEntity)

        var workouts = workoutDao.observeWorkouts().first()

        assertEquals(testWorkoutEntity, workouts[0])

        workoutDao.deleteWorkout(testWorkoutEntity.id)

        workouts = workoutDao.observeWorkouts().first()

        assertEquals(0, workouts.size)
    }

    @Test
    fun workoutDao_UpdateIsComplete_UpdatesCorrectly() = runTest {
        workoutDao.upsertWorkout(testWorkoutEntity)

        workoutDao.updateCompleteWorkout(testWorkoutEntity.id, true)

        val workouts = workoutDao.observeWorkouts().first()

        assertEquals(testWorkoutEntity.copy(isCompleted = true), workouts[0])
    }

    @Test
    fun workoutDao_UpdateDuration_UpdatesCorrectly() = runTest {
        workoutDao.upsertWorkout(testWorkoutEntity)

        workoutDao.updateWorkoutDuration(testWorkoutEntity.id, 3600000)

        val workouts = workoutDao.observeWorkouts().first()

        assertEquals(testWorkoutEntity.copy(duration = 3600000), workouts[0])
    }

    @Test
    fun workoutDao_ObserveFullWorkout_ReturnsCorrectWorkout() = runTest {
        workoutDao.upsertWorkout(testWorkoutEntity)
        exerciseDao.upsertExercise(testExerciseEntity)
        workoutDao.upsertWorkoutExerciseCrossRef(crossRef)
        exerciseSetDao.upsertExerciseSet(testExerciseSet)
        exerciseSetDao.upsertExerciseSet(testExerciseSet.copy(id = 2, date = LocalDate.now().plusDays(2)))

        // first check for incorrect day
        var fullWorkoutInfo = workoutDao.observeFullWorkout(LocalDate.now().plusDays(2)).first()

        assertEquals(null, fullWorkoutInfo)

        fullWorkoutInfo = workoutDao.observeFullWorkout(LocalDate.now()).first()

        assertEquals(testFullWorkout, fullWorkoutInfo)
    }

    @Test
    fun exerciseDao_UpsertAndDeleteExercise_WorksCorrectly() = runTest{
        exerciseDao.upsertExercise(testExerciseEntity)

        var exercises = exerciseDao.observeExercises().first()

        assertEquals(testExerciseEntity, exercises[0])

        exerciseDao.deleteExercise(testExerciseEntity.id)

        exercises = exerciseDao.observeExercises().first()

        assertEquals(0, exercises.size)

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



    private val testWorkoutEntity = WorkoutEntity(
        id = 1,
        name = "Push",
        date = LocalDate.now(),
        duration = 0,
        isCompleted = false
    )

    private val testExerciseEntity = ExerciseEntity(
        id = 1,
        name = "Deadlift",
        type = ExerciseType.Legs.name
    )

    private val crossRef = WorkoutExerciseCrossRef(
        exerciseId = 1,
        workoutId = 1
    )

    private val testExerciseSet = ExerciseSetEntity(
        id = 1,
        exerciseId = 1,
        reps = 12,
        weight = 100f,
        date = LocalDate.now(),
        isCompleted = false
    )

    private val testFullWorkout = WorkoutWithExercisesAndSets(
        workout = testWorkoutEntity,
        exerciseWithSets = listOf(ExerciseWithSets(
            exercise = testExerciseEntity,
            sets = listOf(testExerciseSet, testExerciseSet.copy( id = 2, date = LocalDate.now().plusDays(2))),)
        )
    )

}
