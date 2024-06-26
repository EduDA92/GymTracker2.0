package com.example.gymtracker.ui.exerciseHistory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.ui.model.FilterDates
import com.example.gymtracker.ui.utils.brzyckiOneRepMax
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    exerciseSetRepository: ExerciseSetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId = savedStateHandle["exerciseId"] ?: 0L

    // Timer state by default is data until last 30 days
    private val timeFilterState = MutableStateFlow(FilterDates.OneMonth)

    val exerciseHistoryUiState = combine(exerciseSetRepository.observeExerciseSetHistory(exerciseId), timeFilterState) { list, currentTimeFilter ->

            // All the exercise of the list belong to the same exercise
            /* If there is no history data when accessing to the history of the set, accessing the list
            * will throw outOfBounds exception because we are trying to access empty list
            * so if the list is empty the access will return null and the title will display No Data*/

            val exerciseName = list.getOrNull(0)?.exerciseName ?: "No Data"
            val setHistoryList = mutableListOf<SetHistoryItem>()
            val maxWeightData = mutableMapOf<Float, Float>()
            val totalRepsData = mutableMapOf<Float, Float>()
            val oneRepMaxData = mutableMapOf<Float, Float>()

            // Filter the list by the date selected in the filterChips and groups it by date to group the sets done in the same day
            val filteredDateMap = list.filter { it.exerciseSetDate > timeFilterState.value.time }
                .groupBy { it.exerciseSetDate }

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

            filteredDateMap.forEach { mapEntry ->

                // Create the data for the maxWeight Chart, select the maxWeight done in each day
                maxWeightData[mapEntry.key.toEpochDay().toFloat()] =
                    mapEntry.value.maxByOrNull { it.exerciseSetWeight }?.exerciseSetWeight ?: 0.0f

                // Create the data for the Total reps by day
                totalRepsData[mapEntry.key.toEpochDay().toFloat()] =
                    mapEntry.value.sumOf { it.exerciseSetReps }.toFloat()

                // Create the data for the daily one rep max
                val dailyOneRepMaxList = mapEntry.value.map { brzyckiOneRepMax(it.exerciseSetWeight, it.exerciseSetReps) }
                oneRepMaxData[mapEntry.key.toEpochDay().toFloat()] = dailyOneRepMaxList.max().toFloat()

            }


            ExerciseHistoryUiState.Success(
                HistoryState(
                    exerciseName,
                    currentTimeFilter,
                    setHistoryList.toImmutableList(),
                    maxWeightData.toImmutableMap(),
                    totalRepsData.toImmutableMap(),
                    oneRepMaxData.toImmutableMap()
                )
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ExerciseHistoryUiState.Loading
        )

    fun updateTimeFilterState(selectedTime: FilterDates){

        timeFilterState.update {
            selectedTime
        }

    }


}

sealed interface ExerciseHistoryUiState {

    data class Success(val state: HistoryState) : ExerciseHistoryUiState
    object Loading : ExerciseHistoryUiState

}

data class HistoryState(
    val exerciseName: String,
    val timeFilterState: FilterDates,
    val setHistoryList: ImmutableList<SetHistoryItem>,
    val maxWeightData: ImmutableMap<Float, Float>,
    val totalRepsData: ImmutableMap<Float, Float>,
    val oneRepMaxData: ImmutableMap<Float, Float>
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

