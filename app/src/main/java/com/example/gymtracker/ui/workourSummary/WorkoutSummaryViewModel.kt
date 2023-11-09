package com.example.gymtracker.ui.workourSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutSummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date = _date.asStateFlow()

    /* Mutable flow that handles the state for the workout summary, the workout summary will show a list
    * that consist of the exercises done in the workout along with its top set(set with max weight)
    * the flow can be WorkoutSummaryUiState.Success if there is data in the db for the given day or
    * WorkoutSummaryUiState.EmptyData if there isn't data in the db*/

    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutSummaryUiState: StateFlow<WorkoutSummaryUiState> = _date.flatMapLatest { date ->
        workoutRepository.observeFullWorkout(date).map { workoutAndExercises ->
            if (workoutAndExercises != null) {

                val exerciseSummaryList = mutableListOf<ExerciseSummary>()

                workoutAndExercises.exercisesAndSets.forEach { exerciseAndSets ->

                    exerciseSummaryList.add(
                        ExerciseSummary(
                            name = exerciseAndSets.exerciseName,
                            sets = exerciseAndSets.sets.size,
                            topSet = Pair(
                                first = exerciseAndSets.sets.maxBy { it.weight }.weight,
                                second = exerciseAndSets.sets.maxBy { it.weight }.reps
                            )
                        )
                    )

                }

                WorkoutSummaryUiState.Success(
                    WorkoutSummary(
                        workoutAndExercises.workoutId,
                        workoutAndExercises.workoutName,
                        workoutAndExercises.workoutDate,
                        exerciseSummaryList
                    )
                )
            } else {
                WorkoutSummaryUiState.EmptyData
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        WorkoutSummaryUiState.Loading
    )

    fun prevDate() {
        _date.update { currentDate ->
            currentDate.minusDays(1)
        }
    }

    fun nextDate() {
        _date.update { currentDate ->
            currentDate.plusDays(1)
        }
    }

}

sealed interface WorkoutSummaryUiState {
    data class Success(val workoutSummary: WorkoutSummary) : WorkoutSummaryUiState
    object EmptyData : WorkoutSummaryUiState
    object Loading: WorkoutSummaryUiState
}

data class WorkoutSummary(
    val workoutId: Long,
    val workoutName: String,
    val workoutDate: LocalDate,
    val exercisesSummary: List<ExerciseSummary>
)

data class ExerciseSummary(
    val name: String,
    val sets: Int,
    val topSet: Pair<Float, Int>
)