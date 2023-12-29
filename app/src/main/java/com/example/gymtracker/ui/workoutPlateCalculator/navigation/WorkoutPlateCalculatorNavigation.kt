package com.example.gymtracker.ui.workoutPlateCalculator.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.gymtracker.ui.workoutPlateCalculator.WorkoutPlateCalculatorRoute

const val workoutPlateCalculatorRoute = "workoutPlateCalculatorScreen"

fun NavController.navigateToWorkoutPlateCalculator(){

    this.navigate(workoutPlateCalculatorRoute)

}

fun NavGraphBuilder.workoutPlateCalculatorScreen(
    onBackClick: () -> Unit = {}
){

    composable(route = workoutPlateCalculatorRoute){

        WorkoutPlateCalculatorRoute(
            onBackClick = onBackClick
        )

    }

}