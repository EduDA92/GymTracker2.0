package com.example.gymtracker.ui.workoutCopy

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.model.WorkoutExerciseCrossRef
import com.example.gymtracker.data.repository.ExerciseSetRepository
import com.example.gymtracker.data.repository.WorkoutRepository
import com.example.gymtracker.ui.model.WorkoutAndExercises
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class WorkoutCopyScreenViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val workoutId: Long = savedStateHandle["workoutId"] ?: 0L

    private val todayDate = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    private val workoutList = workoutRepository.observeWorkouts()

    private val _selectedDate = MutableStateFlow(todayDate)

    // Channel to handle back navigation when adding exercises to a workout
    private val copyWorkoutEventChannel = Channel<Boolean>()
    val copyWorkoutEventFlow = copyWorkoutEventChannel.receiveAsFlow()

    val workoutCopyScreenState: StateFlow<WorkoutCopyUiState> = combine(
        workoutList,
        _selectedDate
    ) { workoutList, selectedDate ->

        val selectedWorkout = workoutRepository.getFullWorkout(
            Instant.ofEpochMilli(selectedDate).atZone(ZoneOffset.UTC).toLocalDate()
        )

        WorkoutCopyUiState.Success(
            WorkoutCopyScreenState(
                workoutDates = workoutList.map {
                    it.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                }.toImmutableList(),
                selectedWorkoutData = selectedWorkout!! // this wont be null because dates w/o workout ain't available to select
            )
        )

    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        WorkoutCopyUiState.Loading
    )

    fun updateSelectedDate(selectedDate: Long) {

        _selectedDate.update {
            selectedDate
        }

    }

    fun copyWorkout() {

        viewModelScope.launch {

            val selectedWorkoutData = workoutRepository.getFullWorkout(
                Instant.ofEpochMilli(_selectedDate.value).atZone(ZoneOffset.UTC).toLocalDate()
            )

            selectedWorkoutData?.let{workoutData ->

                workoutData.exercisesAndSets.forEach {

                    workoutRepository.upsertWorkoutExerciseCrossRef(WorkoutExerciseCrossRef(
                        workoutId = workoutId,
                        exerciseId = it.exerciseId
                    ))

                }

            }

            copyWorkoutEventChannel.send(true)

        }


    }

}

sealed interface WorkoutCopyUiState {

    data class Success(val state: WorkoutCopyScreenState) : WorkoutCopyUiState
    object Loading : WorkoutCopyUiState

}

data class WorkoutCopyScreenState(

    val workoutDates: ImmutableList<Long>,
    val selectedWorkoutData: WorkoutAndExercises

)