package com.example.gymtracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymtracker.data.converter.DateConverter
import com.example.gymtracker.data.dao.ExerciseDao
import com.example.gymtracker.data.dao.ExerciseSetDao
import com.example.gymtracker.data.dao.WorkoutDao
import com.example.gymtracker.data.model.ExerciseEntity
import com.example.gymtracker.data.model.ExerciseSetEntity
import com.example.gymtracker.data.model.WorkoutEntity
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        ExerciseSetEntity::class,
        WorkoutExerciseCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class GymTrackerDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
}
