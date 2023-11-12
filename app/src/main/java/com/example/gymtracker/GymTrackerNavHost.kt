package com.example.gymtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.workourSummary.navigation.workoutScreenSummaryRoute
import com.example.gymtracker.ui.workourSummary.navigation.workoutSummaryScreen

@Composable
fun GymTrackerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    NavHost(
        navController = navController,
        startDestination = workoutScreenSummaryRoute,
        modifier = modifier
    ) {

        workoutSummaryScreen()

    }

}