package com.example.gymtracker.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.gymtracker.data.model.ExerciseSetHistoryItem
import com.example.gymtracker.data.model.ExerciseSetEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ExerciseSetDao {

    @Query("SELECT * FROM exerciseSet")
    fun observeExerciseSets(): Flow<List<ExerciseSetEntity>>

    @Upsert
    suspend fun upsertExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Query("DELETE FROM exerciseSet WHERE exerciseSet.id LIKE :exerciseSetId")
    suspend fun deleteExerciseSet(exerciseSetId: Long)

    @Query("DELETE FROM exerciseSet WHERE exerciseSet.date LIKE :date")
    suspend fun deleteExerciseSetFromDate(date: LocalDate)

    @Query("Update exerciseSet SET reps = :setReps, weight = :setWeight WHERE id = :exerciseSetId")
    suspend fun updateExerciseSetData(exerciseSetId: Long, setReps: Int, setWeight: Float)

    @Query("UPDATE exerciseSet SET isCompleted = :isExerciseSetCompleted WHERE id = :exerciseSetId")
    suspend fun updateCompleteExerciseSet(exerciseSetId: Long, isExerciseSetCompleted: Boolean)

    @Query("SELECT id FROM exerciseSet WHERE exerciseId LIKE :exerciseId AND date LIKE :date")
    suspend fun getExerciseSetIdList(exerciseId: Long, date: LocalDate): List<Long>

    @Query("DELETE FROM exerciseSet WHERE id IN (:idList)")
    suspend fun deleteExerciseSets(idList: List<Long>)

    @Query("SELECT exerciseSet.reps, exerciseSet.weight, exerciseSet.date, exercise.name, workout.WorkoutName FROM exerciseSet " +
            "INNER JOIN exercise ON exerciseSet.exerciseId = exercise.id " +
            "INNER JOIN workoutexercisecrossref ON exercise.id = workoutexercisecrossref.exerciseId " +
            "INNER JOIN workout ON workoutexercisecrossref.workoutId = workout.id " +
            "WHERE exercise.id LIKE :exerciseId AND exerciseSet.date LIKE workout.date")
    fun getExerciseSetHistory(exerciseId: Long): Flow<List<ExerciseSetHistoryItem>>

    @Query("SELECT exerciseSet.reps, exerciseSet.weight, exerciseSet.date, exercise.name, workout.WorkoutName FROM exerciseSet " +
            "INNER JOIN exercise ON exerciseSet.exerciseId = exercise.id " +
            "INNER JOIN workoutexercisecrossref ON exercise.id = workoutexercisecrossref.exerciseId " +
            "INNER JOIN workout ON workoutexercisecrossref.workoutId = workout.id " +
            "WHERE exercise.id LIKE :exerciseId AND exerciseSet.date BETWEEN :finalDate AND :actualDate")
    fun getExerciseSetHistoryFromDates(exerciseId: Long, actualDate: LocalDate, finalDate: LocalDate): Flow<List<ExerciseSetHistoryItem>>


}
