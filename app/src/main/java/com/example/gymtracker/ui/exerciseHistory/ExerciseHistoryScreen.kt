package com.example.gymtracker.ui.exerciseHistory

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LineChart
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.FilterDates
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseHistoryRoute(
    modifier: Modifier = Modifier,
    viewModel: ExerciseHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {

    val exerciseHistoryUiState by viewModel.exerciseHistoryUiState.collectAsStateWithLifecycle()

    ExerciseHistoryScreen(
        modifier = modifier,
        exerciseHistoryUiState = exerciseHistoryUiState,
        onBackClick = onBackClick
    )
}

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
fun ExerciseHistoryScreen(
    modifier: Modifier = Modifier,
    exerciseHistoryUiState: ExerciseHistoryUiState,
    onBackClick: () -> Unit = {}
) {

    // Tabs state and titles
    var tabState by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(id = R.string.exercise_history_Tab_History_title),
        stringResource(id = R.string.exercise_history_Tab_Charts_title)
    )

    // Horizontal pager
    val pagerState = rememberPagerState(
        pageCount = { tabTitles.size })

    // if tab state changes change the pager state accordingly
    LaunchedEffect(key1 = tabState) {
        pagerState.animateScrollToPage(tabState)
    }

    //if pagerState changes, change the tab accordingly
    LaunchedEffect(key1 = pagerState.currentPage) {
        tabState = pagerState.currentPage
    }

    when (exerciseHistoryUiState) {
        ExerciseHistoryUiState.Loading -> {

            LoadingState()

        }

        is ExerciseHistoryUiState.Success -> {

            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                WorkoutExerciseListTopAppBar(
                    exerciseName = exerciseHistoryUiState.state.exerciseName,
                    onBackClick = onBackClick
                )

                TabRow(selectedTabIndex = tabState) {

                    tabTitles.forEachIndexed { index, tabTitle ->

                        Tab(
                            selected = index == tabState,
                            onClick = { tabState = index },
                            text = { Text(tabTitle) }
                        )

                    }

                }

                /*
                * TabIndex 0 = History tab
                * TabIndex 1 = Charts tab*/
                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxHeight()) { page ->

                    when (page) {

                        0 -> {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(top = dimensionResource(id = R.dimen.small_dp))
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp))
                            ) {

                                items(
                                    items = exerciseHistoryUiState.state.setHistoryList,
                                    key = { item: SetHistoryItem -> item.workoutDate.toString() }
                                ) {

                                    ExerciseHistoryItem(exerciseHistoryItem = it)

                                }

                            }
                        }

                        1 -> {
                            ChartsComposable(
                                state = exerciseHistoryUiState.state
                            )
                        }
                    }
                }


            }


        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChartsComposable(
    modifier: Modifier = Modifier,
    state: HistoryState
) {

    val segmentedButtonOptions = FilterDates.entries

    LazyColumn(
        modifier = modifier
            .padding(top = dimensionResource(id = R.dimen.small_dp))
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp))
    ) {

        // Filter chips to filter chart data
        item {

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                segmentedButtonOptions.forEach {

                    FilterChip(
                        selected = false,
                        onClick = { /*TODO*/ },
                        label = { Text(it.text) })

                }
            }

        }

        // Max Weight lifted chart
        item {
            LineChart(
                data = state.maxWeightData,
                chartTitle = "Max Weight",
                xAxisLabel = "Day",
                yAxisLabel = "Kg",
                xAxisFormatter = {
                    LocalDate.ofEpochDay(it.toLong()).format(DateTimeFormatter.ofPattern("dd/LLL"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        // Total lifter reps Chart
        item {
            LineChart(
                data = state.totalRepsData,
                chartTitle = "Total Reps",
                xAxisLabel = "Day",
                yAxisLabel = "Reps",
                xAxisFormatter = {
                    LocalDate.ofEpochDay(it.toLong()).format(DateTimeFormatter.ofPattern("dd/LLL"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }


    }


}

@Composable
fun ExerciseHistoryItem(
    modifier: Modifier = Modifier,
    exerciseHistoryItem: SetHistoryItem
) {

    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_dp))
    ) {

        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.small_dp))
        ) {

            // Workout Name
            Text(
                text = exerciseHistoryItem.workoutName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Sets date
            Text(
                text = exerciseHistoryItem.workoutDate.format(DateTimeFormatter.ofPattern("E, MMM d, u"))
            )

            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_dp)))

            // Rep Sets title
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    stringResource(id = R.string.exercise_history_history_item_sets_title),
                    modifier.weight(1f)
                )
                Text(
                    stringResource(id = R.string.exercise_history_history_item_completed_title),
                    modifier.weight(2f)
                )
            }

            Divider()

            exerciseHistoryItem.setHistory.forEachIndexed { index, item ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        (index + 1).toString(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = dimensionResource(id = R.dimen.small_dp))
                    )
                    Text(
                        stringResource(
                            id = R.string.exercise_history_history_item_set_pair_sr,
                            item.weight,
                            item.reps
                        ),
                        modifier = Modifier
                            .weight(2f)
                    )
                }
                if (index < (exerciseHistoryItem.setHistory.size - 1)) {
                    Divider()
                }
            }
        }

    }

}


@Composable
fun WorkoutExerciseListTopAppBar(
    modifier: Modifier = Modifier,
    exerciseName: String,
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
            text = exerciseName,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.small_dp))
                .align(Alignment.Center)
        )
    }

}

@Preview
@Composable
fun ExerciseHistoryScreenPreview() {

    ExerciseHistoryScreen(exerciseHistoryUiState = ExerciseHistoryUiState.Success(historyState))

}

@Preview
@Composable
fun ChartsComposablePreview() {

    ChartsComposable(state = historyState)

}

private val historyState = HistoryState(
    exerciseName = "Squat",
    setHistoryList = persistentListOf(
        SetHistoryItem(
            workoutName = "Monday Workout",
            workoutDate = LocalDate.now(),
            setHistory = persistentListOf(
                SetHistory(99, 999f),
                SetHistory(10, 100f),
                SetHistory(10, 100f)
            )
        ),
        SetHistoryItem(
            workoutName = "Tuesday Workout",
            workoutDate = LocalDate.now().plusDays(1),
            setHistory = persistentListOf(
                SetHistory(15, 150f),
                SetHistory(15, 150f),
                SetHistory(15, 150f)
            )
        )
    ),
    maxWeightData = persistentMapOf(
        LocalDate.now().toEpochDay().toFloat() to 10.0f,
        LocalDate.now().plusDays(2).toEpochDay().toFloat() to 20.0f,
        LocalDate.now().plusDays(4).toEpochDay().toFloat() to 30.0f
    ),
    totalRepsData = persistentMapOf(
        LocalDate.now().toEpochDay().toFloat() to 20.0f,
        LocalDate.now().plusDays(2).toEpochDay().toFloat() to 50.0f,
        LocalDate.now().plusDays(4).toEpochDay().toFloat() to 60.0f
    )
)