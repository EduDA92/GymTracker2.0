package com.example.gymtracker.ui.workoutDiary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import java.time.LocalDate

@Composable
fun WorkoutDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutDiaryViewModel = hiltViewModel()
) {

    val workoutDiaryUiState by viewModel.workoutDiaryUiState.collectAsStateWithLifecycle()
    val workoutNameEditFieldState by viewModel.showEditWorkoutNameField.collectAsStateWithLifecycle()

    WorkoutDiaryScreen(
        modifier = modifier,
        workoutDiaryUiState = workoutDiaryUiState,
        workoutNameEditFieldState = workoutNameEditFieldState,
        updateWorkoutName = viewModel::updateWorkoutName,
        updateWorkoutNameEditFieldState = viewModel::updateShowEditWorkoutNameFieldState
    )
}

@Composable
fun WorkoutDiaryScreen(
    modifier: Modifier = Modifier,
    workoutDiaryUiState: WorkoutDiaryUiState,
    workoutNameEditFieldState: Boolean,
    updateWorkoutName: (String) -> Unit = {},
    updateWorkoutNameEditFieldState: () -> Unit = {}
) {

    when (workoutDiaryUiState) {
        WorkoutDiaryUiState.Loading -> {
            LoadingState(modifier = Modifier)
        }

        is WorkoutDiaryUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                WorkoutTitleField(
                    workoutName = workoutDiaryUiState.diary.workoutName,
                    showEditWorkoutNameField = workoutNameEditFieldState,
                    updateWorkoutName = updateWorkoutName,
                    updateWorkoutNameEditFieldState = updateWorkoutNameEditFieldState
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {

                    items(
                        items = workoutDiaryUiState.diary.exercisesWithReps,
                        key = { item: ExerciseAndSets -> item.exerciseId }
                    ) { item: ExerciseAndSets ->

                        Text(item.exerciseName)
                        item.sets.forEach {
                            Text(it.weight.toString())
                        }

                    }

                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutTitleField(
    modifier: Modifier = Modifier,
    workoutName: String,
    showEditWorkoutNameField: Boolean,
    updateWorkoutName: (String) -> Unit,
    updateWorkoutNameEditFieldState: () -> Unit,
) {

    var workoutNameInput by rememberSaveable {
        mutableStateOf(workoutName)
    }

    val textFieldContentDescription = stringResource(id = R.string.workout_title_edit_field_cd)

    Surface(
        modifier = modifier
            .height(72.dp)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_dp))
    ) {

        when (showEditWorkoutNameField) {
            true -> {
                OutlinedTextField(
                    value = workoutNameInput,
                    onValueChange = { workoutNameInput = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        updateWorkoutName(workoutNameInput)
                        updateWorkoutNameEditFieldState()
                    }),
                    modifier = Modifier.semantics {
                        contentDescription = textFieldContentDescription
                    }
                )
            }

            false -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = workoutName,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.small_dp))
                    )
                    IconButton(onClick = updateWorkoutNameEditFieldState) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_edit_24),
                            contentDescription = stringResource(id = R.string.workout_title_edit_sr)
                        )
                    }
                }

            }
        }
    }

}


@Preview
@Composable
fun WorkoutDiaryScreenPreview() {

    val testExercise1 = ExerciseAndSets(
        exerciseId = 1,
        exerciseName = "DeadLift",
        exerciseType = ExerciseType.Legs,
        sets = listOf(
            ExerciseSet(
                id = 1,
                exerciseId = 1,
                reps = 10,
                weight = 80.5f,
                date = LocalDate.now(),
                isCompleted = true
            ),
            ExerciseSet(
                id = 2,
                exerciseId = 1,
                reps = 8,
                weight = 88.5f,
                date = LocalDate.now(),
                isCompleted = true
            ),
            ExerciseSet(
                id = 3,
                exerciseId = 1,
                reps = 4,
                weight = 90.5f,
                date = LocalDate.now(),
                isCompleted = true
            )
        )
    )

    val testExercise2 = ExerciseAndSets(
        exerciseId = 2,
        exerciseName = "Squat",
        exerciseType = ExerciseType.Legs,
        sets = listOf(
            ExerciseSet(
                id = 4,
                exerciseId = 2,
                reps = 10,
                weight = 80.5f,
                date = LocalDate.now(),
                isCompleted = true
            ),
            ExerciseSet(
                id = 5,
                exerciseId = 2,
                reps = 8,
                weight = 88.5f,
                date = LocalDate.now(),
                isCompleted = true
            ),
            ExerciseSet(
                id = 6,
                exerciseId = 2,
                reps = 4,
                weight = 90.5f,
                date = LocalDate.now(),
                isCompleted = true
            )
        )
    )

    val state = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutName = "Push",
            workoutDate = LocalDate.now(),
            exercisesWithReps = listOf(
                testExercise1, testExercise2
            )
        )
    )

    WorkoutDiaryScreen(
        modifier = Modifier,
        workoutDiaryUiState = state,
        workoutNameEditFieldState = true
    )
}