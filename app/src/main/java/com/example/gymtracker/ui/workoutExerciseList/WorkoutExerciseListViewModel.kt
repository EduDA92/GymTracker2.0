package com.example.gymtracker.ui.workoutExerciseList

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.repository.ExerciseRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
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

    // set to check the list of checked exercise ids.
    private val _checkExercisesList = MutableStateFlow<MutableList<Long>>(mutableListOf())

    // Channel to handle back navigation when adding exercises to a workout
    private val addExercisesEventChannel = Channel<Boolean>()
    val addExerciseEventFlow = addExercisesEventChannel.receiveAsFlow()


    val workoutExerciseListScreenState: StateFlow<WorkoutExerciseListUiState> =
        combine(
            exerciseList,
            _searchedExerciseName,
            _exerciseTypeFilter,
            _checkExercisesList
        ) { exerciseList, exerciseName, exerciseType, checkedList ->

            WorkoutExerciseListUiState.Success(
                WorkoutExerciseListScreenState(
                    exerciseList = exerciseList.filter {
                        it.name.contains(exerciseName, ignoreCase = true) && it.type.name.contains(
                            exerciseType,
                            ignoreCase = true
                        )
                    }.map { ExerciseState(exercise = it, isChecked = checkedList.contains(it.id)) }
                        .toImmutableList(),
                    exerciseTypeFilter = exerciseType,
                    exerciseNameFilter = exerciseName,
                    checkedExercises = checkedList.isNotEmpty()
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

    /* This list will receive an exerciseId, if the id exist it will delete the id from the list
    * else will add the id to the list */
    fun updateExerciseToCheckedList(exerciseId: Long) {

        _checkExercisesList.update {

               val list = it.toMutableList()

                if (list.contains(exerciseId)) {
                    list.remove(exerciseId)
                } else {
                    list.add(exerciseId)
                }

            list

        }

    }

    fun addExercisesToWorkout(){

        val exerciseIdList = _checkExercisesList.value

        viewModelScope.launch {
            exerciseIdList.forEach { exerciseId ->
                workoutRepository.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(
                    workoutId = workoutId,
                    exerciseId = exerciseId
                ))
            }
            // send event
            addExercisesEventChannel.send(true)
        }


    }

}

sealed interface WorkoutExerciseListUiState {

    data class Success(val state: WorkoutExerciseListScreenState) : WorkoutExerciseListUiState
    object Loading : WorkoutExerciseListUiState
}

data class WorkoutExerciseListScreenState(
    val exerciseList: ImmutableList<ExerciseState>,
    val exerciseTypeFilter: String,
    val exerciseNameFilter: String,
    val checkedExercises: Boolean
)

data class ExerciseState(
    val exercise: Exercise,
    val isChecked: Boolean
)

