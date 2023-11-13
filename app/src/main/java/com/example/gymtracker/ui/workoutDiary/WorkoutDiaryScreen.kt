package com.example.gymtracker.ui.workoutDiary

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WorkoutDiaryRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutDiaryViewModel = hiltViewModel()
) {

    Text("Workout ID = " + viewModel.workoutId)

}