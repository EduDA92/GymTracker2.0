package com.example.gymtracker.ui.workoutDiary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.LocalDate

@Composable
fun WorkoutDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutDiaryViewModel = hiltViewModel()
) {

    val workoutDiaryUiState by viewModel.workoutDiaryUiState.collectAsStateWithLifecycle()

    WorkoutDiaryScreen(
        modifier = modifier.fillMaxSize(),
        workoutDiaryUiState = workoutDiaryUiState
    )
}

@Composable
fun WorkoutDiaryScreen(
    modifier: Modifier = Modifier,
    workoutDiaryUiState: WorkoutDiaryUiState
) {
    
    when(workoutDiaryUiState){
        WorkoutDiaryUiState.Loading -> {}
        is WorkoutDiaryUiState.Success -> {
            Text(text = workoutDiaryUiState.diary.workoutId.toString())
        }
    }
}

@Preview
@Composable
fun WorkoutDiaryScreenPreview() {

    val state = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutName = "Push",
            workoutDate = LocalDate.now(),
            exercisesWithReps = emptyList()
        )
    )

    WorkoutDiaryScreen(
        modifier = Modifier.fillMaxSize(),
        workoutDiaryUiState = state
    )
}