package com.example.gymtracker.ui.workourSummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.model.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WorkoutSummaryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {
    

    private val _date = MutableStateFlow(LocalDate.now())
    val date = _date.asStateFlow()

    /* Channel to handle the navigation event when creating a new workout */
    private val createdWorkoutEventChannel = Channel<Long>()
    val createdWorkoutEventFlow = createdWorkoutEventChannel.receiveAsFlow()

    /* Mutable flow that handles the state for the workout summary, the workout summary will show a list
    * that consist of the exercises done in the workout along with its top set(set with max weight)
    * the flow can be WorkoutSummaryUiState.Success if there is data in the db for the given day or
    * WorkoutSummaryUiState.EmptyData if there isn't data in the db*/

    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutSummaryUiState: StateFlow<WorkoutSummaryUiState> = _date.flatMapLatest { date ->
        workoutRepository.observeFullWorkout(date).map { workoutAndExercises ->

            var totalRepsVolume = 0
            var totalWeight = 0f

            if (workoutAndExercises != null) {

                val exerciseSummaryList = mutableListOf<ExerciseSummary>()
                val workoutExerciseDistribution = mutableMapOf<ExerciseType, Int>()

                workoutAndExercises.exercisesAndSets.forEach { exerciseAndSets ->

                    /* Check if the exercise type exist already inside the map and increase the value of its key
                    * or if not exist create a new entry in the map */
                    if (workoutExerciseDistribution.containsKey(exerciseAndSets.exerciseType)) {

                        val previousValue =
                            workoutExerciseDistribution[exerciseAndSets.exerciseType]
                        previousValue?.let {
                            workoutExerciseDistribution[exerciseAndSets.exerciseType] = it + 1
                        }

                    } else {

                        workoutExerciseDistribution[exerciseAndSets.exerciseType] = 1

                    }

                    /* Filter the sets by date because the DAO returns all the sets from an exercise
                    * no matter the date */
                    val filteredSets = exerciseAndSets.sets.filter { it.date == date }

                    filteredSets.forEach { exercise ->
                        totalRepsVolume += exercise.reps
                        totalWeight += exercise.weight
                    }

                    exerciseSummaryList.add(
                        ExerciseSummary(
                            name = exerciseAndSets.exerciseName,
                            sets = filteredSets.size,
                            topSet = Pair(
                                first = filteredSets.maxByOrNull { it.weight }?.weight ?: 0f,
                                second = filteredSets.maxByOrNull { it.weight }?.reps ?: 0
                            )
                        )
                    )

                }

                WorkoutSummaryUiState.Success(
                    WorkoutSummary(
                        workoutId = workoutAndExercises.workoutId,
                        workoutName = workoutAndExercises.workoutName,
                        workoutDate = workoutAndExercises.workoutDate,
                        workoutTotalWeightVolume = totalWeight,
                        workoutTotalRepsVolume = totalRepsVolume,
                        workoutExerciseDistribution = workoutExerciseDistribution,
                        exercisesSummary = exerciseSummaryList
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

    fun createWorkout() {
        viewModelScope.launch {
            createdWorkoutEventChannel.send(
                workoutRepository.upsertWorkout(Workout(date = date.value))
            )
        }
    }

    fun deleteWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workoutId)
        }
    }

}

sealed interface WorkoutSummaryUiState {
    data class Success(val workoutSummary: WorkoutSummary) : WorkoutSummaryUiState
    object EmptyData : WorkoutSummaryUiState
    object Loading : WorkoutSummaryUiState
}

data class WorkoutSummary(
    val workoutId: Long,
    val workoutName: String,
    val workoutDate: LocalDate,
    val workoutTotalWeightVolume: Float,
    val workoutTotalRepsVolume: Int,
    val workoutExerciseDistribution: Map<ExerciseType, Int>,
    val exercisesSummary: List<ExerciseSummary>
)

data class ExerciseSummary(
    val name: String,
    val sets: Int,
    val topSet: Pair<Float, Int>
)