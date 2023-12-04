package com.example.gymtracker.ui.workoutSummary

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutSummaryRoute(
    modifier: Modifier = Modifier,
    navigateToWorkout: (Long) -> Unit = {},
    viewModel: WorkoutSummaryViewModel = hiltViewModel()
) {

    val workoutSummaryUiState by viewModel.workoutSummaryUiState.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    /* This LaunchedEffect will handle the navigation when creating a new workout, when a new workout is created
    * the app will automatically navigate to the newly created workout diary */
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                viewModel.createdWorkoutEventFlow.collect {
                    navigateToWorkout(it)
                }
            }
        }
    }


    WorkoutSummaryScreen(
        modifier = modifier,
        workoutSummaryUiState = workoutSummaryUiState,
        date = date,
        onNextDate = viewModel::nextDate,
        onPrevDate = viewModel::prevDate,
        navigateToWorkout = navigateToWorkout,
        createWorkout = viewModel::createWorkout,
        deleteWorkout = viewModel::deleteWorkout
    )

}

@Composable
fun WorkoutSummaryScreen(
    modifier: Modifier = Modifier,
    workoutSummaryUiState: WorkoutSummaryUiState,
    date: LocalDate,
    onNextDate: () -> Unit = {},
    onPrevDate: () -> Unit = {},
    navigateToWorkout: (Long) -> Unit = {},
    createWorkout: () -> Unit = {},
    deleteWorkout: (Long) -> Unit = {}
) {

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CurrentDateBar(
            date = date,
            onNextDate = onNextDate,
            onPrevDate = onPrevDate
        )

        AnimatedContent(
            targetState = workoutSummaryUiState,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(1500)
                ) togetherWith fadeOut(animationSpec = tween(1500))
            },
            label = "",
            modifier = Modifier
        ) { workoutSummaryUiState ->

            when (workoutSummaryUiState) {
                WorkoutSummaryUiState.EmptyData -> {
                    EmptyState(onCreateWorkoutButton = createWorkout)
                }

                WorkoutSummaryUiState.Loading -> {
                    LoadingState()
                }

                is WorkoutSummaryUiState.Success -> {

                    Column {

                        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_dp)))

                        /* Workout Summary Introductory title */
                        Text(
                            text = stringResource(R.string.workout_screen_today_workout_sr),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = dimensionResource(id = R.dimen.large_db),
                                top = dimensionResource(id = R.dimen.medium_dp),
                                bottom = dimensionResource(id = R.dimen.medium_dp),
                                end = dimensionResource(id = R.dimen.medium_dp)
                            )
                        )


                        WorkoutSummaryCard(
                            workoutId = workoutSummaryUiState.workoutSummary.workoutId,
                            workoutName = workoutSummaryUiState.workoutSummary.workoutName,
                            workoutDate = workoutSummaryUiState.workoutSummary.workoutDate,
                            exerciseSummary = workoutSummaryUiState.workoutSummary.exercisesSummary.toImmutableList(),
                            onEditWorkoutButton = navigateToWorkout,
                            deleteWorkout = deleteWorkout
                        )

                        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_dp)))

                        /* Workout Statistics introductory title */
                        Text(
                            text = stringResource(R.string.workout_screen_today_statistics_sr),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = dimensionResource(id = R.dimen.large_db),
                                top = dimensionResource(id = R.dimen.medium_dp),
                                bottom = dimensionResource(id = R.dimen.medium_dp),
                                end = dimensionResource(id = R.dimen.medium_dp)
                            )
                        )

                        /* This row contains total volume and exerciseDistributionCard */
                        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                            TotalRepsVolumeCard(
                                totalRepsVolume = workoutSummaryUiState.workoutSummary.workoutTotalRepsVolume,
                                totalWeightVolume = workoutSummaryUiState.workoutSummary.workoutTotalWeightVolume,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp)
                            )

                            ExerciseDistributionCard(
                                modifier = Modifier.weight(1f),
                                data = workoutSummaryUiState.workoutSummary.workoutExerciseDistribution.toImmutableMap()
                            )
                        }

                    }

                }

            }
        }

    }
}

@Composable
fun WorkoutSummaryCard(
    modifier: Modifier = Modifier,
    workoutId: Long,
    workoutName: String,
    workoutDate: LocalDate,
    exerciseSummary: ImmutableList<ExerciseSummary>,
    onEditWorkoutButton: (Long) -> Unit = {},
    deleteWorkout: (Long) -> Unit = {}
) {

    var isDropdownMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_dp)
        )
    ) {

        /* Card title contains, workout name, date and option button */
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(4f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = workoutName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = workoutDate.toString(),
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp
                )
            }

            /* This box contains the options button and the dropdown menu */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {

                IconButton(
                    onClick = { isDropdownMenuVisible = true },
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(id = R.string.card_options_button_sr)
                    )
                }

                /* Dropdown Menu for the options button, this menu will anchor to the box parent composable */
                DropdownMenu(
                    expanded = isDropdownMenuVisible,
                    onDismissRequest = { isDropdownMenuVisible = false }) {

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.dropdown_menu_delete_workout_sr),
                                color = Color.Red
                            )
                        },
                        onClick = {
                            deleteWorkout(workoutId)
                            isDropdownMenuVisible = false
                        })

                }
            }
        }

        /* This column holds the exercise summary for the workout */
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp))) {

            /* Title for the exercise list */
            Surface(shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp))) {
                Row(
                    modifier = modifier.padding(dimensionResource(id = R.dimen.medium_dp))
                ) {

                    Text(
                        text = stringResource(R.string.card_title_exercise_sr),
                        modifier = Modifier.weight(5f)
                    )

                    Text(
                        text = stringResource(R.string.card_title_sets_sr),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(R.string.card_title_top_set_sr),
                        modifier = Modifier
                            .weight(3f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            /* Exercise list */
            exerciseSummary.forEachIndexed { index, exercise ->

                Row(
                    modifier = modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = exercise.name,
                        modifier = Modifier.weight(5f)
                    )

                    Text(
                        text = exercise.sets.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.top_set_pair_sr,
                            exercise.topSet.first,
                            exercise.topSet.second
                        ),
                        modifier = Modifier
                            .weight(3f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }

                if (index < exerciseSummary.size - 1) {
                    Divider()
                }

            }
        }

        OutlinedButton(
            onClick = { onEditWorkoutButton(workoutId) },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(dimensionResource(id = R.dimen.medium_dp)),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.medium_dp)),
        ) {

            Text(stringResource(R.string.card_edit_workout_button_sr))

        }


    }


}
@Composable
fun ExerciseDistributionCard(
    modifier: Modifier = Modifier,
    data: ImmutableMap<ExerciseType, Int>
) {

    val textMeasurer = rememberTextMeasurer()
    val color = MaterialTheme.colorScheme.primary


    Card(
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_dp)
        )
    ) {
        if (data.isNotEmpty()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dimensionResource(id = R.dimen.medium_dp))
            ) {


                /* The Space for each bar is the total space divided by the number of exerciseTypes inside the map
                * each bar has a top and bottom padding so the bar height is the maxBarSpace minus two times the padding
                *
                * The height or amplitude of each bar is a percentage of the max value, meaning the maxValue will have the
                * maxHeight and so on.*/

                /* Bar Parameters */
                val padding = 12f
                val maxBarWidth = size.width.minus(padding.times(2)).div(1.5f)
                val maxBarSpace = size.height.div(data.size)
                val maxBarHeight = maxBarSpace.minus(padding.times(2))
                val maxValue = data.maxOf { it.value }
                val invMaxValue = 1f / maxValue

                /* Multiply by this to get the % of the max width for each bar */
                val barWidthScaleFactor = invMaxValue * maxBarWidth

                val textStyle = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                var prevY = padding
                /* The position of the first text will be the padding plus half of the bar height and
                * in order to center the text subtract the height approx height of the font */
                var prevTextY = padding.plus(maxBarHeight.div(2)).minus(10.sp.toPx())

                data.forEach { (exerciseType, numberOfExercises) ->

                    drawRect(
                        color = color,
                        topLeft = Offset(x = 0f, y = prevY),
                        size = Size(
                            width = numberOfExercises.times(barWidthScaleFactor),
                            height = maxBarHeight
                        )
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = exerciseType.name,
                        style = textStyle,
                        topLeft = Offset(
                            x = numberOfExercises.times(barWidthScaleFactor).plus(padding),
                            y = prevTextY
                        )
                    )

                    prevY += maxBarSpace
                    prevTextY += maxBarSpace
                }
            }
        } else {

            Text(
                stringResource(R.string.exercise_distribution_card_empty_data_sr),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight(Alignment.CenterVertically)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

        }
    }

}

@Composable
fun TotalRepsVolumeCard(
    modifier: Modifier = Modifier,
    totalRepsVolume: Int,
    totalWeightVolume: Float
) {

    Card(
        modifier = modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_dp)
        )
    ) {

        Text(
            text = stringResource(R.string.total_reps_weight_card_reps_title_sr),
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.large_db),
                start = dimensionResource(id = R.dimen.medium_dp),
                end = dimensionResource(id = R.dimen.medium_dp)
            )
        )

        Text(
            text = stringResource(
                id = R.string.total_reps_weight_card_reps_volume_sr,
                totalRepsVolume
            ),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.medium_dp),
                start = dimensionResource(id = R.dimen.medium_dp)
            )
        )

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.large_db)))
        Text(
            text = stringResource(R.string.total_reps_weight_card_weight_title_sr),
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.medium_dp),
                start = dimensionResource(id = R.dimen.medium_dp)
            )
        )
        Text(
            text = stringResource(
                id = R.string.total_reps_weight_card_weight_volume_sr,
                totalWeightVolume
            ),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                top = dimensionResource(id = R.dimen.medium_dp),
                start = dimensionResource(id = R.dimen.medium_dp),
                end = dimensionResource(id = R.dimen.medium_dp),
                bottom = dimensionResource(id = R.dimen.medium_dp)
            )
        )

    }

}


@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateWorkoutButton: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentHeight(Alignment.CenterVertically),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.no_workout_data_sr),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp))
        )

        Button(onClick = { onCreateWorkoutButton() }) {
            Text(text = stringResource(id = R.string.create_workout_button_sr))
        }

    }
}

@Composable
fun CurrentDateBar(
    modifier: Modifier = Modifier,
    date: LocalDate,
    onNextDate: () -> Unit = {},
    onPrevDate: () -> Unit = {}
) {

    Surface(
        modifier = modifier,
        shadowElevation = dimensionResource(id = R.dimen.small_dp)
    ) {

        /* DateTime formatter to dd/MM */
        val dateTimeFormatter = DateTimeFormatter.ofPattern("d/MMM")

        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onPrevDate,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(
                        id = R.string.prev_date_icon_sr
                    )
                )
            }

            Text(
                text = when (date) {
                    LocalDate.now() -> stringResource(id = R.string.today_sr)
                    else -> {
                        date.format(dateTimeFormatter)
                    }
                },
                modifier = Modifier
                    .weight(2f)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            IconButton(
                onClick = onNextDate,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(
                        id = R.string.next_date_icon_sr
                    )
                )
            }
        }
    }

}

@Preview
@Composable
fun WorkoutSummaryPreview() {

    val workoutSummary = WorkoutSummaryUiState.Success(
        workoutSummary = WorkoutSummary(
            workoutId = 1,
            workoutName = "Push",
            workoutDate = LocalDate.now(),
            workoutTotalWeightVolume = 300000.68f,
            workoutTotalRepsVolume = 10000,
            workoutExerciseDistribution = mutableMapOf(
                ExerciseType.Arms to 2,
                ExerciseType.Chest to 3,
                ExerciseType.Shoulders to 2,
                ExerciseType.FullBody to 4
            ),
            exercisesSummary = listOf(
                ExerciseSummary(
                    name = "Squat",
                    sets = 999,
                    topSet = Pair(999.75f, 100)
                ),
                ExerciseSummary(
                    name = "Deadlift",
                    sets = 3,
                    topSet = Pair(100f, 8)
                ),
                ExerciseSummary(
                    name = "Lunge",
                    sets = 3,
                    topSet = Pair(100f, 8)
                ),
                ExerciseSummary(
                    name = "Squat",
                    sets = 300,
                    topSet = Pair(100.75f, 10)
                ),
                ExerciseSummary(
                    name = "Deadlift",
                    sets = 3,
                    topSet = Pair(100f, 8)
                ),
                ExerciseSummary(
                    name = "Lunge",
                    sets = 3,
                    topSet = Pair(100f, 8)
                )
            )
        )
    )

    GymTrackerTheme {
        WorkoutSummaryScreen(
            modifier = Modifier,
            workoutSummary,
            LocalDate.now(),
        )
    }
}