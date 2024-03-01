package com.example.gymtracker.ui.exerciseHistory.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.gymtracker.ui.exerciseHistory.ExerciseHistoryRoute

private const val exerciseIdArg = "exerciseId"

fun NavController.navigateToExerciseHistory(exerciseId: Long) {

    this.navigate("exerciseHistoryRoute/$exerciseId")

}


fun NavGraphBuilder.exerciseHistoryScreen(
    onBackClick: () -> Unit = {},
) {

    composable(
        route = "exerciseHistoryRoute/{$exerciseIdArg}",
        arguments = listOf(
            navArgument(exerciseIdArg) { type = NavType.LongType }
        )
    ) {

        ExerciseHistoryRoute(
            onBackClick = onBackClick
        )

    }

}