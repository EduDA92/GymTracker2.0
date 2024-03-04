package com.example.gymtracker.ui.exerciseHistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.ExerciseSetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    exerciseSetRepository: ExerciseSetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId = savedStateHandle["exerciseId"] ?: 0L

    val exerciseHistoryUiState =
        exerciseSetRepository.observeExerciseSetHistory(exerciseId).map { list ->

            // All the exercise of the list belong to the same exercise
            val exerciseName = list[0].exerciseName
            val setHistoryList = mutableListOf<SetHistoryItem>()

            // Group the sets by date and model the Data to a list of SetHistoryItems
            list.groupBy { it.exerciseSetDate }.forEach { mapEntry ->

                setHistoryList.add(
                    SetHistoryItem(
                        workoutName = mapEntry.value[0].workoutName,
                        workoutDate = mapEntry.key,
                        setHistory = mapEntry.value.map {
                            SetHistory(
                                reps = it.exerciseSetReps,
                                weight = it.exerciseSetWeight
                            )
                        }.toImmutableList()
                    )
                )

            }

            // Sort the list by date from most recent to oldest
            setHistoryList.sortByDescending { it.workoutDate }


            ExerciseHistoryUiState.Success(
                HistoryState(exerciseName, setHistoryList.toImmutableList())
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ExerciseHistoryUiState.Loading
        )


}

sealed interface ExerciseHistoryUiState {

    data class Success(val state: HistoryState) : ExerciseHistoryUiState
    object Loading : ExerciseHistoryUiState

}

data class HistoryState(
    val exerciseName: String,
    val setHistoryList: ImmutableList<SetHistoryItem>
)

data class SetHistoryItem(
    val workoutName: String,
    val workoutDate: LocalDate,
    val setHistory: ImmutableList<SetHistory>
)

data class SetHistory(
    val reps: Int,
    val weight: Float
)

