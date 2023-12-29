package com.example.gymtracker.ui.workoutPlateCalculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutPlateCalculatorRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
){

    WorkoutPlateCalculatorScreen()

}

@Composable
fun WorkoutPlateCalculatorScreen(
    modifier: Modifier = Modifier
){

    Text("Plate calculator", modifier = modifier.fillMaxSize())

}

@Preview
@Composable
fun WorkoutPlateCalculatorScreenPreview(){
    WorkoutPlateCalculatorScreen()
}