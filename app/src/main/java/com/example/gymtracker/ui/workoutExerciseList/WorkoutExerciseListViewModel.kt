package com.example.gymtracker.ui.workoutExerciseList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WorkoutExerciseListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val workoutId: Long = savedStateHandle["workoutId"] ?: 0L

    // Exercise list state
    val exerciseList = exerciseRepository.observeExercises()

    // State to handle the filter by name
    private val _searchedExerciseName = MutableStateFlow("")

    // State to handle the filter by exercise type
    private val _exerciseTypeFilter = MutableStateFlow(ExerciseType.Other)


    val workoutExerciseListScreenState: StateFlow<WorkoutExerciseListUiState> =
        combine(
            exerciseList,
            _searchedExerciseName,
            _exerciseTypeFilter
        ) { exerciseList, exerciseName, exerciseType ->


            WorkoutExerciseListUiState.Success(
                WorkoutExerciseListScreenState(
                    exerciseList = exerciseList.filter {
                        if(exerciseType == ExerciseType.Other){
                            it.name.contains(exerciseName)
                        } else {
                            it.name.contains(exerciseName) && it.type == exerciseType
                        }
                    },
                    exerciseTypeFiler = exerciseType,
                    exerciseNameFilter = exerciseName
                )
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WorkoutExerciseListUiState.Loading
        )

    fun updateExerciseTypeFilter(exerciseType: ExerciseType) {
        _exerciseTypeFilter.update {
            exerciseType
        }
    }

    fun updateSearchedExerciseName(searchedName: String) {
        _searchedExerciseName.update {
            searchedName
        }
    }

}

sealed interface WorkoutExerciseListUiState {

    data class Success(val state: WorkoutExerciseListScreenState) : WorkoutExerciseListUiState
    object Loading : WorkoutExerciseListUiState
}

data class WorkoutExerciseListScreenState(
    val exerciseList: List<Exercise>,
    val exerciseTypeFiler: ExerciseType,
    val exerciseNameFilter: String
)