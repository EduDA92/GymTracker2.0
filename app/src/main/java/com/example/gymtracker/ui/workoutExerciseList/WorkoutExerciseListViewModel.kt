package com.example.gymtracker.ui.workoutExerciseList

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutExerciseListViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val workoutId: Long = savedStateHandle["workoutId"] ?: 0L

    // Exercise list state
    private val exerciseList = exerciseRepository.observeExercises()

    // State to handle the filter by name
    private val _searchedExerciseName = MutableStateFlow("")

    // State to handle the filter by exercise type
    private val _exerciseTypeFilter = MutableStateFlow("")


    val workoutExerciseListScreenState: StateFlow<WorkoutExerciseListUiState> =
        combine(
            exerciseList,
            _searchedExerciseName,
            _exerciseTypeFilter
        ) { exerciseList, exerciseName, exerciseType ->

            WorkoutExerciseListUiState.Success(
                WorkoutExerciseListScreenState(
                    exerciseList = exerciseList.filter {
                        it.name.contains(exerciseName, ignoreCase = true) && it.type.name.contains(exerciseType, ignoreCase = true)
                    }.toImmutableList(),
                    exerciseTypeFilter = exerciseType,
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
            exerciseType.name
        }
    }

    fun clearExerciseTypeFilter() {
        _exerciseTypeFilter.update {
            ""
        }
    }

    fun updateSearchedExerciseName(searchedName: String) {
        _searchedExerciseName.update {
            searchedName
        }
    }

    fun clearSearchedExerciseName() {
        _searchedExerciseName.update {
            ""
        }
    }

    fun createExercise(exerciseName: String, exerciseType: ExerciseType) {

        viewModelScope.launch {
            exerciseRepository.upsertExercise(
                Exercise(
                    name = exerciseName,
                    type = exerciseType
                )
            )
        }

    }


}

sealed interface WorkoutExerciseListUiState {

    data class Success(val state: WorkoutExerciseListScreenState) : WorkoutExerciseListUiState
    object Loading : WorkoutExerciseListUiState
}

data class WorkoutExerciseListScreenState(
    val exerciseList: ImmutableList<Exercise>,
    val exerciseTypeFilter: String,
    val exerciseNameFilter: String
)