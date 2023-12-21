package com.example.gymtracker.ui.workoutCopy.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymtracker.ui.workoutCopy.WorkoutCopyRoute

private const val workoutIdArg = "workoutId"

fun NavController.navigateToWorkoutCopy(workoutId: Long) {

    this.navigate("workoutCopyRoute/$workoutId")

}

fun NavGraphBuilder.workoutCopyScreen(onBackClick: () -> Unit) {

    composable(
        route = "workoutCopyRoute/{$workoutIdArg}",
        arguments = listOf(
            navArgument(workoutIdArg) {
                type = NavType.LongType
            }
        )
    ) {

        WorkoutCopyRoute(onBackClick = onBackClick)

    }

}