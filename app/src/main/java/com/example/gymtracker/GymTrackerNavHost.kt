package com.example.gymtracker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.gymtracker.ui.workoutCopy.navigation.navigateToWorkoutCopy
import com.example.gymtracker.ui.workoutCopy.navigation.workoutCopyScreen
import com.example.gymtracker.ui.workoutSummary.navigation.workoutScreenSummaryRoute
import com.example.gymtracker.ui.workoutSummary.navigation.workoutSummaryScreen
import com.example.gymtracker.ui.workoutDiary.navigation.navigateToWorkoutDiary
import com.example.gymtracker.ui.workoutDiary.navigation.workoutDiaryScreen
import com.example.gymtracker.ui.workoutExerciseList.navigation.navigateToWorkoutExerciseList
import com.example.gymtracker.ui.workoutExerciseList.navigation.workoutExerciseListScreen
import com.example.gymtracker.ui.workoutPlateCalculator.navigation.navigateToWorkoutPlateCalculator
import com.example.gymtracker.ui.workoutPlateCalculator.navigation.workoutPlateCalculatorScreen

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

        workoutSummaryScreen(navigateToWorkout = navController::navigateToWorkoutDiary)

        workoutDiaryScreen(
            onBackClick = navController::popBackStack,
            navigateToExerciseList = navController::navigateToWorkoutExerciseList,
            navigateToCopyWorkout = navController::navigateToWorkoutCopy,
            navigateToWorkoutPlateCalculator = navController::navigateToWorkoutPlateCalculator
        )

        workoutExerciseListScreen(onBackClick = navController::popBackStack)

        workoutCopyScreen(onBackClick = navController::popBackStack)

        workoutPlateCalculatorScreen(onBackClick = navController::popBackStack)

    }

}