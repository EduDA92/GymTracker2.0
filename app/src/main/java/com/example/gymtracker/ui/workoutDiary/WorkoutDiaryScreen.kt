package com.example.gymtracker.ui.workoutDiary

import android.content.Intent
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.services.RestTimerService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Composable
fun WorkoutDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutDiaryViewModel = hiltViewModel(),
    navigateToExerciseList: (Long) -> Unit = {},
    navigateToCopyWorkout: (Long) -> Unit = {},
    navigateToWorkoutPlateCalculator: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    val workoutDiaryUiState by viewModel.workoutDiaryUiState.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()

    WorkoutDiaryScreen(
        modifier = modifier,
        workoutDiaryUiState = workoutDiaryUiState,
        timerState = timerState,
        updateWorkoutName = remember { { viewModel.updateWorkoutName(it) } },
        navigateToExerciseList = navigateToExerciseList,
        navigateToCopyWorkout = navigateToCopyWorkout,
        navigateToWorkoutPlateCalculator = navigateToWorkoutPlateCalculator,
        deleteExercise = remember {
            { workoutId, workoutDate, exerciseId ->
                viewModel.deleteExerciseFromWorkout(
                    workoutId,
                    workoutDate,
                    exerciseId
                )
            }
        },
        onBackClick = onBackClick,
        deleteExerciseSet = remember { { viewModel.deleteExerciseSet(it) } },
        addExerciseSet = remember {
            { exerciseId, workoutDate ->
                viewModel.addExerciseSet(
                    exerciseId,
                    workoutDate
                )
            }
        },
        updateExerciseSetIsCompleted = remember {
            { exerciseSetId, isCompleted ->
                viewModel.updateExerciseSetIsCompleted(
                    exerciseSetId,
                    isCompleted
                )
            }
        },
        updateExerciseSetData = remember {
            { exerciseSetId, setReps, setWeight ->
                viewModel.updateExerciseSetData(
                    exerciseSetId,
                    setReps,
                    setWeight
                )
            }
        }
    )
}

@Composable
fun WorkoutDiaryScreen(
    modifier: Modifier = Modifier,
    workoutDiaryUiState: WorkoutDiaryUiState,
    timerState: TimerState,
    updateWorkoutName: (String) -> Unit = {},
    navigateToExerciseList: (Long) -> Unit = {},
    navigateToCopyWorkout: (Long) -> Unit = {},
    navigateToWorkoutPlateCalculator: () -> Unit = {},
    deleteExercise: (Long, LocalDate, Long) -> Unit = { _, _, _ -> },
    onBackClick: () -> Unit = {},
    deleteExerciseSet: (Long) -> Unit = {},
    addExerciseSet: (Long, LocalDate) -> Unit = { _, _ -> },
    updateExerciseSetIsCompleted: (Long, Boolean) -> Unit = { _, _ -> },
    updateExerciseSetData: (Long, Int, Float) -> Unit = { _, _, _ -> }
) {

    val context = LocalContext.current

    var workoutNameEditFieldState by rememberSaveable {
        mutableStateOf(false)
    }

    var restTimerDialogState by rememberSaveable {
        mutableStateOf(false)
    }

    var restTimerExplanationDialogState by rememberSaveable {
        mutableStateOf(false)
    }

    /* Permission Callback */
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            restTimerDialogState = true
        }

    }

    when (workoutDiaryUiState) {
        WorkoutDiaryUiState.Loading -> {
            LoadingState(modifier = modifier)
        }

        is WorkoutDiaryUiState.Success -> {

            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {

                LazyColumn(
                    modifier = modifier
                        .padding(top = dimensionResource(id = R.dimen.small_dp))
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp))
                ) {

                    item {
                        WorkoutDiaryToolbar(
                            workoutName = workoutDiaryUiState.diary.workoutName,
                            showEditWorkoutNameField = workoutNameEditFieldState,
                            updateWorkoutName = updateWorkoutName,
                            updateWorkoutNameEditFieldState = {
                                workoutNameEditFieldState = !workoutNameEditFieldState
                            },
                            onBackClick = onBackClick
                        )
                    }

                    items(
                        items = workoutDiaryUiState.diary.exercisesWithReps,
                        key = { item: ExerciseAndSets -> item.exerciseId }
                    ) { item: ExerciseAndSets ->

                        ExerciseAndSets(
                            workoutId = workoutDiaryUiState.diary.workoutId,
                            workoutDate = workoutDiaryUiState.diary.workoutDate.toString(),
                            exerciseId = item.exerciseId,
                            exerciseName = item.exerciseName,
                            exerciseSets = item.sets,
                            deleteExercise = deleteExercise,
                            addExerciseSet = addExerciseSet,
                            deleteExerciseSet = deleteExerciseSet,
                            updateExerciseSetIsCompleted = updateExerciseSetIsCompleted,
                            updateExerciseSetData = updateExerciseSetData,
                        )

                    }

                    /* This row contains the Add Exercise and Copy Workout buttons */
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = dimensionResource(id = R.dimen.small_dp),
                                    end = dimensionResource(id = R.dimen.small_dp)
                                )
                        ) {
                            Button(
                                onClick = remember { { navigateToExerciseList(workoutDiaryUiState.diary.workoutId) } },
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp)),
                                modifier = Modifier.weight(3f)
                            ) {
                                Text(text = stringResource(id = R.string.add_exercise_button))
                            }
                            IconButton(onClick = remember {
                                {
                                    navigateToCopyWorkout(
                                        workoutDiaryUiState.diary.workoutId
                                    )
                                }
                            }) {
                                Icon(
                                    painterResource(id = R.drawable.outline_file_copy_24),
                                    contentDescription = stringResource(id = R.string.copy_workout_button_sr)
                                )
                            }
                        }
                    }

                }

                /* Rest timer dialog */
                when {
                    restTimerDialogState -> {
                        RestTimerDialog(
                            onDismissRequest = { restTimerDialogState = false },
                            timerState = timerState,
                            workoutId = workoutDiaryUiState.diary.workoutId
                        )
                    }
                }

                /* Rest timer dialog explanation */
                when {
                    restTimerExplanationDialogState -> {
                        RestTimerExplanationDialog(onConfirm = {
                            requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                            restTimerExplanationDialogState = false
                        },
                            onDismiss = { restTimerExplanationDialogState = false })
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row {
                        OutlinedButton(
                            onClick = { navigateToWorkoutPlateCalculator() },
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.medium_dp),
                                    top = dimensionResource(id = R.dimen.medium_dp),
                                    bottom = dimensionResource(id = R.dimen.medium_dp),
                                    end = dimensionResource(id = R.dimen.small_dp)
                                )
                                .weight(1f),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_calculate_24),
                                contentDescription = stringResource(id = R.string.workout_diary_plate_calculator_button_icon_cd),
                                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.medium_dp))
                            )
                            Text(text = stringResource(id = R.string.workout_diary_plate_calculator_button_sr))
                        }

                        OutlinedButton(
                            onClick = remember {
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        when {
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                POST_NOTIFICATIONS
                                            ) == PackageManager.PERMISSION_GRANTED -> {
                                                restTimerDialogState = true
                                            }

                                            ActivityCompat.shouldShowRequestPermissionRationale(
                                                context.getActivity(),
                                                POST_NOTIFICATIONS
                                            ) -> restTimerExplanationDialogState = true

                                            else -> requestPermissionLauncher.launch(
                                                POST_NOTIFICATIONS
                                            )

                                        }
                                    } else {
                                        restTimerDialogState = true
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(id = R.dimen.small_dp),
                                    top = dimensionResource(id = R.dimen.medium_dp),
                                    bottom = dimensionResource(id = R.dimen.medium_dp),
                                    end = dimensionResource(id = R.dimen.medium_dp)
                                )
                                .weight(1f),
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.outline_timer_24),
                                contentDescription = stringResource(id = R.string.workout_diary_timer_button_icon_cd),
                                modifier = Modifier.padding(end = dimensionResource(id = R.dimen.medium_dp))
                            )
                            Text(text = stringResource(id = R.string.workout_diary_rest_timer_button_sr))

                        }
                    }

                }


            }


        }
    }
}


@Composable
fun ExerciseAndSets(
    modifier: Modifier = Modifier,
    workoutId: Long,
    workoutDate: String,
    exerciseId: Long,
    exerciseName: String,
    exerciseSets: ImmutableList<ExerciseSet>,
    deleteExercise: (Long, LocalDate, Long) -> Unit = { _, _, _ -> },
    addExerciseSet: (Long, LocalDate) -> Unit = { _, _ -> },
    deleteExerciseSet: (Long) -> Unit = {},
    updateExerciseSetData: (Long, Int, Float) -> Unit = { _, _, _ -> },
    updateExerciseSetIsCompleted: (Long, Boolean) -> Unit
) {

    var isDropdownMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }


    Surface(modifier = modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp))
        ) {

            /* Exercise Name and Menu */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(5f)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {

                    IconButton(
                        onClick = { isDropdownMenuVisible = true }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_more_horiz_24),
                            contentDescription = stringResource(id = R.string.exercise_dropdown_menu_sr)
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownMenuVisible,
                        onDismissRequest = { isDropdownMenuVisible = false }) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(id = R.string.dropdown_menu_delete_exercise_sr),
                                    color = Color.Red
                                )
                            },
                            onClick = {
                                deleteExercise(workoutId, LocalDate.parse(workoutDate), exerciseId)
                                isDropdownMenuVisible = false
                            })

                    }
                }
            }

            /* Show Kg and Reps Header only if there is sets created */
            if (exerciseSets.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(id = R.string.kg_sr),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = stringResource(id = R.string.reps_sr),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(2f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    /* Placeholder to align the Kg and Reps text properly */
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))


                }

                Divider(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.small_dp)))
            }

            /* Exercise sets, expected behaviour:
            * When clicking on Complete exercise set button if the set is already completed nothing happens
            * if isn't completed then update set weight and reps data and the completed state
            *
            * When clicking in the text view then it means the user wants to edit the field so the exercise set
            * state pass to completed = false to let the user update the set
            *
            * TODO = ALL SETS SHOULD BE MARKED AS COMPLETED WHEN ENDING THE WORKOUT*/

            exerciseSets.forEach { exerciseSet ->

                key(exerciseSet.id) {

                    ExerciseSetItem(
                        exerciseSet = exerciseSet,
                        deleteExerciseSet = deleteExerciseSet,
                        updateExerciseSetData = updateExerciseSetData,
                        updateExerciseSetIsCompleted = updateExerciseSetIsCompleted
                    )

                }

            }

            /* Add set and history buttons */
            Row(modifier = Modifier.fillMaxWidth()) {

                TextButton(
                    onClick = { addExerciseSet(exerciseId, LocalDate.parse(workoutDate)) },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = stringResource(id = R.string.add_set_button_icon_cd)
                    )
                    Text(text = stringResource(id = R.string.add_set_button_sr))
                }

                TextButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_history_24),
                        contentDescription = stringResource(id = R.string.history_button_icon_cd)
                    )
                    Text(text = stringResource(id = R.string.history_button_sr))
                }
            }

        }

    }
}

@Composable()
fun ExerciseSetItem(
    modifier: Modifier = Modifier,
    exerciseSet: ExerciseSet,
    deleteExerciseSet: (Long) -> Unit = {},
    updateExerciseSetData: (Long, Int, Float) -> Unit = { _, _, _ -> },
    updateExerciseSetIsCompleted: (Long, Boolean) -> Unit = { _, _ -> }

) {

    val repsEditTextsCd = stringResource(id = R.string.workout_diary_reps_edit_text_cd)
    val weightEditTextsCd = stringResource(id = R.string.workout_diary_weight_edit_text_cd)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        var weight by rememberSaveable {
            mutableStateOf(exerciseSet.weight.toString())
        }

        var reps by rememberSaveable {
            mutableStateOf(exerciseSet.reps.toString())
        }

        when (exerciseSet.isCompleted) {
            true -> {
                Text(
                    text = exerciseSet.weight.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .clickable {
                            updateExerciseSetIsCompleted(
                                exerciseSet.id,
                                false
                            )
                        }
                )

                Text(
                    text = exerciseSet.reps.toString(),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .weight(2f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .clickable {
                            updateExerciseSetIsCompleted(
                                exerciseSet.id,
                                false
                            )
                        }
                )
            }

            false -> {
                BasicTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .wrapContentHeight(Alignment.CenterVertically)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp)
                        ) {

                            innerTextField()
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .requiredSize(width = 56.dp, height = 46.dp)
                        .semantics { contentDescription = weightEditTextsCd }
                )

                BasicTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .wrapContentHeight(Alignment.CenterVertically)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp)
                        ) {

                            innerTextField()
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .requiredSize(width = 56.dp, height = 46.dp)
                        .semantics { contentDescription = repsEditTextsCd }
                )
            }
        }

        IconButton(
            onClick = {
                if (!exerciseSet.isCompleted) {
                    updateExerciseSetData(
                        exerciseSet.id,
                        reps.toInt(),
                        weight.toFloat()
                    )
                    updateExerciseSetIsCompleted(exerciseSet.id, true)
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            when (exerciseSet.isCompleted) {
                true -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(id = R.string.complete_exercise_set_button_cd)
                    )
                }

                false -> {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = stringResource(id = R.string.incomplete_exercise_set_button_cd)
                    )
                }
            }
        }

        IconButton(
            onClick = {
                deleteExerciseSet(exerciseSet.id)
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(
                    id = R.string.delete_exercise_set_button_cd
                )
            )
        }
    }

}


@Composable
fun WorkoutDiaryToolbar(
    modifier: Modifier = Modifier,
    workoutName: String,
    showEditWorkoutNameField: Boolean,
    onBackClick: () -> Unit = {},
    updateWorkoutName: (String) -> Unit = {},
    updateWorkoutNameEditFieldState: () -> Unit = {},
) {

    var workoutNameInput by rememberSaveable {
        mutableStateOf(workoutName)
    }

    val textFieldContentDescription = stringResource(id = R.string.workout_title_edit_field_cd)

    Surface(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        Box{

            IconButton(onClick = onBackClick, modifier.align(Alignment.CenterStart)) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button_cd)
                )
            }

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
                        trailingIcon = {
                            IconButton(onClick = updateWorkoutNameEditFieldState) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = stringResource(id = R.string.diary_screen_edit_name_clear_button_sr)
                                )
                            }
                        },
                        modifier = Modifier
                            .semantics {
                                contentDescription = textFieldContentDescription
                            }
                            .align(Alignment.Center)
                            .padding(
                                start = dimensionResource(id = R.dimen.small_dp),
                                end = dimensionResource(id = R.dimen.small_dp)
                            )
                    )
                }

                false -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = workoutName,
                            fontSize = 20.sp,
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

            // TODO: workout timer
            Text("Timer", modifier = Modifier.align(Alignment.CenterEnd).padding(dimensionResource(id = R.dimen.medium_dp)))


        }
    }

}

@Composable
fun RestTimerDialog(
    onDismissRequest: () -> Unit = {},
    timerState: TimerState,
    workoutId: Long
) {

    val context = LocalContext.current
    val startServiceIntent = Intent(context, RestTimerService::class.java).apply {
        putExtra(RestTimerService.TIMER_DURATION, 100000L)
        putExtra(RestTimerService.TIMER_INTERVAL, 100L)
        putExtra(RestTimerService.WORKOUT_ID, workoutId)
    }


    Dialog(onDismissRequest = onDismissRequest) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(dimensionResource(id = R.dimen.medium_dp))
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {

                Text(timerState.timerValue.toString())
                Text(timerState.timerState.toString())

                Button(onClick = {
                    context.startService(startServiceIntent)
                }) {
                    Text("Start Timer")
                }

                Button(onClick = {
                    onDismissRequest()
                    context.stopService(Intent(context, RestTimerService::class.java))
                }) {
                    Text("Dismiss")
                }
            }


        }

    }

}

@Composable
fun RestTimerExplanationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.notification_rationale_title)) },
        text = { Text(stringResource(id = R.string.notification_rationale_body)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.confirm_sr))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.dismiss_sr))
            }
        })
}


@Preview
@Composable
fun WorkoutDiaryScreenPreview() {

    val testExercise1 = ExerciseAndSets(
        exerciseId = 1,
        exerciseName = "DeadLift",
        exerciseType = ExerciseType.Legs,
        sets = persistentListOf(
            ExerciseSet(
                id = 1,
                exerciseId = 1,
                reps = 10,
                weight = 100f,
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
                isCompleted = false
            )
        )
    )

    val testExercise2 = ExerciseAndSets(
        exerciseId = 2,
        exerciseName = "Squat",
        exerciseType = ExerciseType.Legs,
        sets = persistentListOf(
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
                isCompleted = false
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
            exercisesWithReps = persistentListOf(
                testExercise1, testExercise2
            )
        )
    )

    WorkoutDiaryScreen(
        modifier = Modifier,
        workoutDiaryUiState = state,
        timerState = TimerState(0, false),
    )
}

@Preview
@Composable
fun RestTimerDialogPreview() {
    RestTimerDialog(workoutId = 1, timerState = TimerState(0, false))
}

fun Context.getActivity(): Activity {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}