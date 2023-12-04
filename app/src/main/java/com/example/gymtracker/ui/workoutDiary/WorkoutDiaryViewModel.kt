package com.example.gymtracker.ui.workoutDiary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutDiaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseSetRepository: ExerciseSetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    val workoutId: Long = savedStateHandle["workoutId"] ?: 0L


    private val _showEditWorkoutNameField = MutableStateFlow(false)
    val showEditWorkoutNameField = _showEditWorkoutNameField.asStateFlow()

    val workoutDiaryUiState =
        workoutRepository.observeFullWorkoutFromId(workoutId).map { workoutAndExercises ->

            val exercisesWithReps = mutableListOf<ExerciseAndSets>()

            workoutAndExercises.exercisesAndSets.forEach { exerciseAndSets ->

                exercisesWithReps.add(
                    ExerciseAndSets(
                        exerciseId = exerciseAndSets.exerciseId,
                        exerciseName = exerciseAndSets.exerciseName,
                        exerciseType = exerciseAndSets.exerciseType,
                        sets = exerciseAndSets.sets.filter { it.date == workoutAndExercises.workoutDate }
                    )
                )

            }

            WorkoutDiaryUiState.Success(
                WorkoutDiary(
                    workoutId = workoutAndExercises.workoutId,
                    workoutName = workoutAndExercises.workoutName,
                    workoutDate = workoutAndExercises.workoutDate,
                    exercisesWithReps = exercisesWithReps
                )
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WorkoutDiaryUiState.Loading
        )

    fun updateShowEditWorkoutNameFieldState() {
        _showEditWorkoutNameField.update {
            !it
        }
    }


    fun updateWorkoutName(workoutName: String) {

        viewModelScope.launch {
            workoutRepository.updateWorkoutName(
                workoutId = (workoutDiaryUiState.value as WorkoutDiaryUiState.Success).diary.workoutId,
                workoutName = workoutName
            )
        }

    }


    fun deleteExerciseSet(exerciseSetId: Long) {

        viewModelScope.launch {
            exerciseSetRepository.deleteExerciseSet(exerciseSetId)
        }

    }

    /* Updates the reps and text fields for the given exercise set */
    fun updateExerciseSetData(exerciseSetId: Long, setReps: Int, setWeight: Float) {

        viewModelScope.launch {
            exerciseSetRepository.updateExerciseSetData(exerciseSetId, setReps, setWeight)
        }

    }

    /* Updates the completed state of the exercise to the opposite of the actual state */
    fun updateExerciseSetIsCompleted(exerciseSetId: Long, isCompleted: Boolean) {

        viewModelScope.launch {
            exerciseSetRepository.updateCompleteExerciseSet(exerciseSetId, isCompleted)
        }

    }

    /* When deleting an exercise from the workout the exercise sets done already in the workout
    * should be deleted too, have in mind that the exercise itself its not being deleted, just
    * the "connection" of exercise and workout */
    fun deleteExerciseFromWorkout(workoutId: Long, workoutDate: LocalDate, exerciseId: Long) {

        viewModelScope.launch {

            val exerciseSetIds = exerciseSetRepository.getExerciseSetIdList(exerciseId, workoutDate)

            workoutRepository.deleteWorkoutExerciseCrossRef(workoutId, exerciseId)
            exerciseSetRepository.deleteExerciseSets(exerciseSetIds)

        }

    }

    fun addExerciseSet(exerciseId: Long, workoutDate: LocalDate) {

        viewModelScope.launch {

            exerciseSetRepository.upsertExerciseSet(
                ExerciseSet(
                    exerciseId = exerciseId,
                    date = workoutDate
                )
            )

        }

    }


}

sealed interface WorkoutDiaryUiState {

    data class Success(val diary: WorkoutDiary) : WorkoutDiaryUiState
    object Loading : WorkoutDiaryUiState
}

data class WorkoutDiary(
    val workoutId: Long,
    val workoutName: String,
    val workoutDate: LocalDate,
    val exercisesWithReps: List<ExerciseAndSets>
)