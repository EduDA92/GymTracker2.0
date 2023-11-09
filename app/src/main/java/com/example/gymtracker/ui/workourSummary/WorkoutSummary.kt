package com.example.gymtracker.ui.workourSummary

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WorkoutSummaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutSummaryViewModel = hiltViewModel()
) {

    val workoutSummaryUiState by viewModel.workoutSummaryUiState.collectAsStateWithLifecycle()
    val date by viewModel.date.collectAsStateWithLifecycle()

    WorkoutSummary(
        modifier = modifier,
        workoutSummaryUiState = workoutSummaryUiState,
        date = date,
        onNextDate = viewModel::nextDate,
        onPrevDate = viewModel::prevDate
    )

}

@Composable
fun WorkoutSummary(
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
            WorkoutSummaryUiState.EmptyData -> Log.e("DATA", "EMPTY DATA")
            is WorkoutSummaryUiState.Success -> Log.e("DATA", "SUCCESS DATA")
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

        /* DateTime formmater to dd/MM */
        val dateTimeFormatter = DateTimeFormatter.ofPattern("d/MMM")

        Row(
            modifier = Modifier.fillMaxWidth(),
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

    val workoutSummary = WorkoutSummaryUiState.EmptyData

    GymTrackerTheme {
        WorkoutSummary(modifier = Modifier.fillMaxSize(), workoutSummary, LocalDate.now())
    }
}