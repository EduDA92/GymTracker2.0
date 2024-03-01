package com.example.gymtracker.ui.exerciseHistory

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExerciseHistoryRoute(
    viewModel: ExerciseHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
){
    ExerciseHistoryScreen()
}

@Composable
fun ExerciseHistoryScreen(){
    Text("HELLo", modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
fun ExerciseHistoryScreenPreview(){

    ExerciseHistoryScreen()

}