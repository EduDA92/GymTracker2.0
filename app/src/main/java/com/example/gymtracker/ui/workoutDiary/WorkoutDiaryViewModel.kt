package com.example.gymtracker.ui.workoutDiary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.ExerciseAndSets
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutDiaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(

) {

    val workoutId: Long = savedStateHandle["workoutId"] ?: 0

    val workoutDiaryUiState: StateFlow<WorkoutDiaryUiState> =
        workoutRepository.observeFullWorkoutFromId(workoutId).map { workoutAndExercises ->

            // Filter exercise sets that doesn't belong to the workout date

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


    fun updateWorkoutName(workoutName: String) {

        viewModelScope.launch {
            workoutRepository.updateWorkoutName(
                workoutId = (workoutDiaryUiState.value as WorkoutDiaryUiState.Success).diary.workoutId,
                workoutName = workoutName
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