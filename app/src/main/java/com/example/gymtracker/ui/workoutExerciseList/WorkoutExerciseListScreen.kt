package com.example.gymtracker.ui.workoutExerciseList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType

@Composable
fun WorkoutExerciseListRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutExerciseListViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

    val workoutExerciseListState by viewModel.workoutExerciseListScreenState.collectAsStateWithLifecycle()

    WorkoutExerciseListScreen(
        modifier = modifier,
        workoutExerciseListState = workoutExerciseListState,
        updateSearchedExerciseName = viewModel::updateSearchedExerciseName,
        onBackClick = onBackClick
    )

}


@Composable
fun WorkoutExerciseListScreen(
    modifier: Modifier = Modifier,
    workoutExerciseListState: WorkoutExerciseListUiState,
    updateSearchedExerciseName: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
) {

    when (workoutExerciseListState) {
        WorkoutExerciseListUiState.Loading -> {
            LoadingState(modifier = modifier)
        }

        is WorkoutExerciseListUiState.Success -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                WorkoutExerciseListTopAppBar(onBackClick = onBackClick)

                SearchFilterBar(
                    searchedExerciseName = workoutExerciseListState.state.exerciseNameFilter,
                    updateSearchedExerciseName = updateSearchedExerciseName
                )

                LazyColumn(modifier = Modifier) {

                    items(
                        items = workoutExerciseListState.state.exerciseList,
                        key = { item: Exercise -> item.id })
                    {

                        Text(text = it.name)

                    }


                }

                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))
                ) {
                    Text(text = stringResource(id = R.string.workout_exercise_list_create_exercise_sr))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchFilterBar(
    modifier: Modifier = Modifier,
    searchedExerciseName: String,
    updateSearchedExerciseName: (String) -> Unit = {},
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        FlowColumn {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchedExerciseName,
                    onValueChange = { updateSearchedExerciseName(it) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.medium_dp))
                        .weight(7f)
                )
                IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Outlined.Menu, contentDescription = "")
                }
            }

            Row {
                // TODO here show filter type and clear button
            }


        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutExerciseListTopAppBar(
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
fun WorkoutExerciseListScreenPreview() {

    WorkoutExerciseListScreen(
        workoutExerciseListState =
        WorkoutExerciseListUiState.Success(
            WorkoutExerciseListScreenState(
                exerciseList = listOf(
                    Exercise(
                        id= 0,
                        name = "exercise1",
                        type = ExerciseType.Arms
                    ),
                    Exercise(
                        id = 1,
                        name = "exercise2",
                        type = ExerciseType.Legs
                    ),
                    Exercise(
                        id = 2,
                        name = "exercise2",
                        type = ExerciseType.Chest
                    )
                ),
                exerciseTypeFiler = ExerciseType.Arms,
                exerciseNameFilter = "Test"
            )
        )
    )

}