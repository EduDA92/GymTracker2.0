package com.example.gymtracker.ui.workoutDiary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryRoute

private const val workoutIdArg = "workoutId"
private val uri = "https://www.gymtracker.com"
fun NavController.navigateToWorkoutDiary(workoutId: Long) {

    this.navigate("workoutDiaryRoute/$workoutId")

}

fun NavGraphBuilder.workoutDiaryScreen(
    onBackClick: () -> Unit = {},
    navigateToExerciseList: (Long) -> Unit = {},
    navigateToCopyWorkout: (Long) -> Unit = {},
    navigateToWorkoutPlateCalculator: () -> Unit = {},
    navigateToExerciseHistory: (Long) -> Unit = {},
) {
    composable(
        route = "workoutDiaryRoute/{$workoutIdArg}",
        deepLinks = listOf(navDeepLink { uriPattern = "$uri/{$workoutIdArg}" }),
        arguments = listOf(
            navArgument(workoutIdArg) { type = NavType.LongType }
        )
    ) {

        WorkoutDiaryRoute(
            onBackClick = onBackClick,
            navigateToExerciseList = navigateToExerciseList,
            navigateToCopyWorkout = navigateToCopyWorkout,
            navigateToWorkoutPlateCalculator = navigateToWorkoutPlateCalculator,
            navigateToExerciseHistory = navigateToExerciseHistory
        )

    }
}