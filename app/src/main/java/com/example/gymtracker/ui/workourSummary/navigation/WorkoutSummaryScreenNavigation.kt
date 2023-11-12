package com.example.gymtracker.ui.workourSummary.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.gymtracker.ui.workourSummary.WorkoutSummaryRoute

const val workoutScreenSummaryRoute = "workoutScreenSummary"
fun NavGraphBuilder.workoutSummaryScreen(){
    composable(
        route = workoutScreenSummaryRoute,
    ){
        WorkoutSummaryRoute()
    }
}