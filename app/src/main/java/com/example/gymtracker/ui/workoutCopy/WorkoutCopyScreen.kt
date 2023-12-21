package com.example.gymtracker.ui.workoutCopy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.model.WorkoutAndExercises
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun WorkoutCopyRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutCopyScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

    val state by viewModel.workoutCopyScreenState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    // navigate back when the workout is copied
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.copyWorkoutEventFlow.collect {
                    onBackClick()
                }

            }
        }
    }

    WorkoutCopyScreen(
        modifier = modifier,
        workoutCopyScreenState = state,
        updateSelectedDate = viewModel::updateSelectedDate,
        copyWorkout = viewModel::copyWorkout,
        onBackClick = onBackClick
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutCopyScreen(
    modifier: Modifier = Modifier,
    workoutCopyScreenState: WorkoutCopyUiState,
    copyWorkout: () -> Unit = {},
    updateSelectedDate: (Long) -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
            .toEpochMilli(),
    )

    // Launched Effect to update the selected day and send it to the viewmodel
    LaunchedEffect(key1 = datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            updateSelectedDate(Instant.ofEpochMilli(it).toEpochMilli())
        }

    }


    when (workoutCopyScreenState) {
        WorkoutCopyUiState.Loading -> LoadingState()

        is WorkoutCopyUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {

                WorkoutCopyScreenTopAppBar(onBackClick = onBackClick)

                DatePicker(state = datePickerState, dateValidator = {
                    workoutCopyScreenState.state.workoutDates.contains(it)
                })
                Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_dp)))
                SelectedWorkoutInfo(
                    workoutData = workoutCopyScreenState.state.selectedWorkoutData,
                    copyWorkout = copyWorkout
                )

            }
        }
    }

}

@Composable
fun SelectedWorkoutInfo(
    modifier: Modifier = Modifier,
    workoutData: WorkoutAndExercises,
    copyWorkout: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = workoutData.workoutName, fontSize = 20.sp)
            Divider()

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.medium_dp))
            ) {

                workoutData.exercisesAndSets.forEach {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = dimensionResource(id = R.dimen.medium_dp),
                                end = dimensionResource(id = R.dimen.medium_dp)
                            )
                    ) {
                        Text(
                            text = it.exerciseName,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(
                                id = R.string.workout_copy_series_number_sr,
                                it.sets.size
                            ), fontSize = 18.sp,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentWidth(Alignment.End)
                        )
                    }

                }

            }
        }

        Button(
            onClick = { copyWorkout()}, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Text(text = stringResource(id = R.string.copy_workout_copy_button_sr))
        }
    }
}

@Composable
fun WorkoutCopyScreenTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {

    Box(modifier = modifier.fillMaxWidth()) {

        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.back_button_cd)
            )
        }

        Text(
            text = "Exercises",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.small_dp))
                .align(Alignment.Center)
        )
    }

}


@Preview
@Composable
fun WorkoutCopyScreenPreview() {

    val workoutDates = persistentListOf<Long>(
        LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        LocalDate.now().plusDays(3).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    )

    val workoutData = WorkoutAndExercises(
        workoutId = 1,
        workoutName = "PPL",
        workoutDate = LocalDate.now(),
        workoutDuration = 0,
        workoutCompleted = true,
        exercisesAndSets = persistentListOf(
            ExerciseAndSets(
                exerciseId = 1,
                exerciseName = "Squat",
                exerciseType = ExerciseType.Legs,
                sets = persistentListOf()
            ),
            ExerciseAndSets(
                exerciseId = 2,
                exerciseName = "BenchPress",
                exerciseType = ExerciseType.Chest,
                sets = persistentListOf()
            ),
            ExerciseAndSets(
                exerciseId = 3,
                exerciseName = "Deadlift",
                exerciseType = ExerciseType.Legs,
                sets = persistentListOf()
            )
        )
    )

    WorkoutCopyScreen(
        workoutCopyScreenState = WorkoutCopyUiState.Success(
            WorkoutCopyScreenState(
                workoutDates = workoutDates,
                selectedWorkoutData = workoutData
            )
        )
    )

}