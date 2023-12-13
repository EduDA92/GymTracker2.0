package com.example.gymtracker.ui.workoutExerciseList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.Exercise
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.utils.recomposeHighlighter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WorkoutExerciseListRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutExerciseListViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

    val workoutExerciseListState by viewModel.workoutExerciseListScreenState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    // navigate back when the exercises are added to the workout
    LaunchedEffect(lifecycleOwner.lifecycle){
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.addExerciseEventFlow.collect{
                   onBackClick()
                }

            }
        }
    }

    WorkoutExerciseListScreen(
        modifier = modifier,
        workoutExerciseListState = workoutExerciseListState,
        updateSearchedExerciseName = viewModel::updateSearchedExerciseName,
        clearSearchedExerciseName = viewModel::clearSearchedExerciseName,
        createExercise = viewModel::createExercise,
        updateExerciseTypeFilter = viewModel::updateExerciseTypeFilter,
        clearExerciseTypeFilter = viewModel::clearExerciseTypeFilter,
        updateExerciseToCheckedList = viewModel::updateExerciseToCheckedList,
        addExercisesToWorkout = viewModel::addExercisesToWorkout,
        onBackClick = onBackClick
    )

}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkoutExerciseListScreen(
    modifier: Modifier = Modifier,
    workoutExerciseListState: WorkoutExerciseListUiState,
    updateSearchedExerciseName: (String) -> Unit = {},
    clearSearchedExerciseName: () -> Unit = {},
    createExercise: (String, ExerciseType) -> Unit = { _, _ -> },
    updateExerciseTypeFilter: (ExerciseType) -> Unit = {},
    clearExerciseTypeFilter: () -> Unit = {},
    updateExerciseToCheckedList: (Long) -> Unit = {},
    addExercisesToWorkout: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val exerciseTypeList = remember { ExerciseType.entries.toImmutableList() }


    val showScrollTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }



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
                    exerciseTypeList = exerciseTypeList,
                    updateExerciseTypeFilter = updateExerciseTypeFilter,
                    clearExerciseTypeFilter = clearExerciseTypeFilter
                )
                Box(modifier = Modifier.fillMaxSize()) {

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        items(
                            items = workoutExerciseListState.state.exerciseList,
                            key = { item: ExerciseState-> item.exercise.id })
                        {

                            ExerciseItem(
                                exerciseId = it.exercise.id,
                                exerciseName = it.exercise.name,
                                exerciseType = it.exercise.type.name,
                                isChecked = it.isChecked,
                                updateExerciseToCheckedList = updateExerciseToCheckedList,
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .recomposeHighlighter()
                            )

                        }

                        // Create exercise button
                        item {

                            Button(
                                onClick = { showBottomSheet = true },
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))
                            ) {
                                Text(text = stringResource(id = R.string.workout_exercise_list_create_exercise_sr))
                            }

                        }
                    }

                    /* This will show button to scroll to the top */
                    this@Column.AnimatedVisibility(visible = showScrollTopButton) {
                        Button(
                            onClick = { scope.launch { listState.animateScrollToItem(0) } },
                            shape = CircleShape,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowUp,
                                contentDescription = stringResource(id = R.string.workout_exercise_list_scroll_top_button_cd)
                            )
                        }
                    }

                    Button(
                        onClick = {addExercisesToWorkout()},
                        modifier = Modifier.align(Alignment.BottomCenter),
                        enabled = workoutExerciseListState.state.checkedExercises
                    ){
                        Text(text = stringResource(id = R.string.workout_exercise_list_add_exercises_button_sr))
                    }

                }

                /* Bottom sheet handles the exercise creation */
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {


                        ExerciseNameCreation(
                            showBottomSheet = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            },
                            exerciseTypeList = exerciseTypeList,
                            createExercise = createExercise
                        )

                        Spacer(modifier = Modifier.size(56.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseNameCreation(
    modifier: Modifier = Modifier,
    showBottomSheet: () -> Unit = {},
    exerciseTypeList: ImmutableList<ExerciseType>,
    createExercise: (String, ExerciseType) -> Unit = { _, _ -> }
) {

    val exerciseNameTextFieldCd =
        stringResource(id = R.string.workout_exercise_list_exercise_name_text_field_cd)
    val exerciseTypeTextFieldCd =
        stringResource(id = R.string.workout_exercise_list_exercise_type_text_field_cd)

    var exerciseName by rememberSaveable {
        mutableStateOf("")
    }

    var exerciseType by rememberSaveable {
        mutableStateOf("")
    }

    val buttonState by remember {
        derivedStateOf {
            exerciseName.isNotEmpty() && exerciseType.isNotEmpty()
        }
    }


    var dropdownMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }


    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.workout_exercise_list_exercise_name_sr),
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.medium_dp))
        )

        OutlinedTextField(
            value = exerciseName,
            onValueChange = { exerciseName = it },
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.semantics { contentDescription = exerciseNameTextFieldCd }
        )

        Text(
            text = stringResource(id = R.string.workout_exercise_list_exercise_type_sr),
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.medium_dp))
        )

        ExposedDropdownMenuBox(
            expanded = dropdownMenuExpanded,
            onExpandedChange = { dropdownMenuExpanded = !dropdownMenuExpanded }) {

            OutlinedTextField(
                value = exerciseType,
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { dropdownMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = stringResource(id = R.string.workout_exercise_list_drop_menu_button_cd)
                        )
                    }
                },
                modifier = Modifier
                    .menuAnchor()
                    .semantics { contentDescription = exerciseTypeTextFieldCd }
            )
            ExposedDropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = { dropdownMenuExpanded = false }) {

                exerciseTypeList.forEach { exerciseTypeOption ->

                    DropdownMenuItem(
                        text = { Text(text = exerciseTypeOption.name) },
                        onClick = {
                            exerciseType = exerciseTypeOption.name
                            dropdownMenuExpanded = false
                        })

                }

            }

        }

        Button(
            onClick = {
                createExercise(exerciseName, ExerciseType.valueOf(exerciseType))
                showBottomSheet()
            },
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.medium_dp)),
            enabled = buttonState
        ) {
            Text(stringResource(id = R.string.workout_exercise_list_save_exercise_text_sr))
        }

        OutlinedButton(onClick = {
            showBottomSheet()
        }) {
            Text(stringResource(id = R.string.workout_exercise_list_cancel_exercise_creation_text_sr))
        }

    }

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterBar(
    modifier: Modifier = Modifier,
    searchedExerciseName: String,
    exerciseTypeFilter: String,
    exerciseTypeList: ImmutableList<ExerciseType>,
    updateSearchedExerciseName: (String) -> Unit = {},
    clearSearchedExerciseName: () -> Unit = {},
    updateExerciseTypeFilter: (ExerciseType) -> Unit = {},
    clearExerciseTypeFilter: () -> Unit = {},
) {

    var showFilterList by rememberSaveable {
        mutableStateOf(false)
    }

    val searchNameTexFieldCd = stringResource(id = R.string.workout_exercise_list_search_name_cd)

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
                        if (searchedExerciseName.isNotEmpty()) {
                            IconButton(onClick = clearSearchedExerciseName) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = stringResource(id = R.string.workout_exercise_list_clear_name_cd)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.medium_dp))
                        .weight(7f)
                        .semantics { contentDescription = searchNameTexFieldCd }
                )
                IconButton(
                    onClick = { showFilterList = !showFilterList },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = stringResource(id = R.string.workout_exercise_list_filter_button_cd)
                    )
                }
            }

            /* Filter chips to filter the exercise list by exercise type */
            AnimatedVisibility(visible = showFilterList) {

                FlowRow(modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp))) {
                    exerciseTypeList.forEach { exerciseType ->

                        FilterChip(
                            selected = exerciseTypeFilter == exerciseType.name,
                            onClick = {
                                if (exerciseTypeFilter == exerciseType.name) {
                                    clearExerciseTypeFilter()
                                } else {
                                    updateExerciseTypeFilter(exerciseType)
                                }
                            },
                            label = { Text(exerciseType.name) },
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.small_dp))
                        )

                    }
                }

            }


        }

    }

}

@Composable
fun ExerciseItem(
    modifier: Modifier = Modifier,
    exerciseId: Long,
    exerciseName: String,
    exerciseType: String,
    isChecked: Boolean,
    updateExerciseToCheckedList: (Long) -> Unit = {}
) {

    Surface(modifier = modifier
        .fillMaxWidth()
        .clickable { updateExerciseToCheckedList(exerciseId) }) {
        Row {

            Column {
                Text(
                    text = exerciseName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = exerciseType,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                )
            }

            if (isChecked) {
                Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "test")
            }

        }
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

/*@Preview
@Composable
fun ExerciseNameCreationPreview() {

    ExerciseNameCreation(
        exerciseTypeList = ExerciseType.entries.toImmutableList(),
    )

}*/


@Preview
@Composable
fun WorkoutExerciseListScreenPreview() {

    WorkoutExerciseListScreen(
        workoutExerciseListState =
        WorkoutExerciseListUiState.Success(
            WorkoutExerciseListScreenState(
                exerciseList = persistentListOf(
                    ExerciseState(exercise = Exercise(
                        id = 0,
                        name = "exercise1",
                        type = ExerciseType.Arms
                    ), isChecked = true)
                    ,
                    ExerciseState(exercise = Exercise(
                        id = 1,
                        name = "exercise2",
                        type = ExerciseType.Legs
                    ), isChecked = false),
                    ExerciseState(exercise = Exercise(
                        id = 2,
                        name = "exercise3",
                        type = ExerciseType.Chest
                    ), isChecked = false)
                ),
                exerciseTypeFilter = ExerciseType.Arms.name,
                exerciseNameFilter = "Test",
                checkedExercises = false
            )
        )
    )

}