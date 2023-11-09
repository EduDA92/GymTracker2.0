package com.example.gymtracker.ui.workourSummary

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.theme.GymTrackerTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle

@Composable
fun WorkoutSummaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutSummaryViewModel = hiltViewModel()
) {

    val workoutSummaryUiState by viewModel.workoutSummaryUiState.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()

    WorkoutSummaryScreen(
        modifier = modifier,
        workoutSummaryUiState = workoutSummaryUiState,
        date = date,
        onNextDate = viewModel::nextDate,
        onPrevDate = viewModel::prevDate
    )

}

@Composable
fun WorkoutSummaryScreen(
    modifier: Modifier = Modifier,
    workoutSummaryUiState: WorkoutSummaryUiState,
    date: LocalDate,
    onNextDate: () -> Unit = {},
    onPrevDate: () -> Unit = {}
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        CurrentDateBar(
            modifier = Modifier,
            date = date,
            onNextDate = onNextDate,
            onPrevDate = onPrevDate
        )

        when (workoutSummaryUiState) {
            WorkoutSummaryUiState.EmptyData -> {

                EmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                )

            }

            is WorkoutSummaryUiState.Success -> {

                WorkoutSummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    workoutName = workoutSummaryUiState.workoutSummary.workoutName,
                    workoutDate = workoutSummaryUiState.workoutSummary.workoutDate,
                    exerciseSummary = workoutSummaryUiState.workoutSummary.exercisesSummary.toImmutableList()
                )

            }

            WorkoutSummaryUiState.Loading -> {
                LoadingState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }

    }
}

@Composable
fun WorkoutSummaryCard(
    modifier: Modifier = Modifier,
    workoutName: String,
    workoutDate: LocalDate,
    exerciseSummary: ImmutableList<ExerciseSummary>,
    onOptionsButton: (Long) -> Unit = {},
) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.small_dp)
        )
    ) {

        /* Card title contains, workout name, date and option button */
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(4f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = workoutName)
                Text(text = workoutDate.toString())
            }

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(id = R.string.card_options_button_sr)
                )
            }
        }


    }

}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator()

        Text(
            text = stringResource(id = R.string.loading_data_sr),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp))
        )

    }

}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    onCreateWorkoutButton: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.no_workout_data_sr),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp))
        )

        Button(onClick = onCreateWorkoutButton) {
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
            exercisesSummary = listOf(
                ExerciseSummary(
                    name = "Squat",
                    sets = 3,
                    topSet = Pair(100f, 8)
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
        WorkoutSummaryScreen(modifier = Modifier.fillMaxSize(), workoutSummary, LocalDate.now())
    }
}