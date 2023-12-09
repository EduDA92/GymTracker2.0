package com.example.gymtracker.ui.workoutExerciseList

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

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
        clearSearchedExerciseName = viewModel::clearSearchedExerciseName,
        updateExerciseTypeFilter = viewModel::updateExerciseTypeFilter,
        clearExerciseTypeFilter = viewModel::clearExerciseTypeFilter,
        onBackClick = onBackClick
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutExerciseListScreen(
    modifier: Modifier = Modifier,
    workoutExerciseListState: WorkoutExerciseListUiState,
    updateSearchedExerciseName: (String) -> Unit = {},
    clearSearchedExerciseName: () -> Unit = {},
    updateExerciseTypeFilter: (ExerciseType) -> Unit = {},
    clearExerciseTypeFilter: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

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
                    updateSearchedExerciseName = updateSearchedExerciseName,
                    clearSearchedExerciseName = clearSearchedExerciseName,
                    exerciseTypeFilter = workoutExerciseListState.state.exerciseTypeFilter,
                    showFilterList = { showBottomSheet = it },
                    updateExerciseTypeFilter = updateExerciseTypeFilter,
                    clearExerciseTypeFilter = clearExerciseTypeFilter
                )

                LazyColumn(modifier = Modifier) {

                    items(
                        items = workoutExerciseListState.state.exerciseList,
                        key = { item: Exercise -> item.id })
                    {

                        // TODO add animated item placement here!
                        Text(text = it.name)

                    }


                }

                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))
                ) {
                    Text(text = stringResource(id = R.string.workout_exercise_list_create_exercise_sr))
                }

                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {

                        Text(text = "HELLO")

                        Button(onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }) {
                            Text("Save")
                        }

                        OutlinedButton(onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }) {
                            Text("Cancel")
                        }

                    }
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
    exerciseTypeFilter: String,
    showFilterList: (Boolean) -> Unit = {},
    updateSearchedExerciseName: (String) -> Unit = {},
    clearSearchedExerciseName: () -> Unit = {},
    updateExerciseTypeFilter: (ExerciseType) -> Unit = {},
    clearExerciseTypeFilter: () -> Unit = {},
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
                    trailingIcon = {
                        IconButton(onClick =  clearSearchedExerciseName ) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = stringResource(id = R.string.workout_exercise_list_clear_name_cd)
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.medium_dp))
                        .weight(7f)
                )
                IconButton(onClick = { showFilterList(true) }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Outlined.Menu,
                        contentDescription = stringResource(id = R.string.workout_exercise_list_filter_button_cd))
                }
            }

            AnimatedVisibility(visible = exerciseTypeFilter.isNotEmpty()) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {},
                        modifier.padding(start = dimensionResource(id = R.dimen.medium_dp))
                    ) {
                        Text(text = exerciseTypeFilter)
                    }

                    OutlinedButton(
                        onClick = clearExerciseTypeFilter,
                        modifier.padding(start = dimensionResource(id = R.dimen.medium_dp))
                    ) {
                        Text(text = stringResource(id = R.string.workout_exercise_list_clear_type_filter_sr))
                    }

                }

            }


        }

    }

}

@Composable
fun exerciseItem(
    modifier: Modifier = Modifier
) {

    Surface(modifier = modifier) {
        //TODO
    }

}


@Composable
fun WorkoutExerciseListTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {

    Box(modifier = modifier.fillMaxWidth()) {

        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
                exerciseList = persistentListOf(
                    Exercise(
                        id = 0,
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
                exerciseTypeFilter = ExerciseType.Arms.name,
                exerciseNameFilter = "Test"
            )
        )
    )

}