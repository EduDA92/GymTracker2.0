package com.example.gymtracker.ui.workoutDiary.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryRoute

private const val workoutIdArg = "workoutId"
fun NavController.navigateToWorkoutDiary(workoutId: Long) {

    this.navigate("workoutDiaryRoute/$workoutId")

}

fun NavGraphBuilder.workoutDiaryScreen(
    onBackClick: () -> Unit = {},
    navigateToExerciseList: (Long) -> Unit = {},
    navigateToCopyWorkout: (Long) -> Unit = {}
) {
    composable(
        route = "workoutDiaryRoute/{$workoutIdArg}",
        arguments = listOf(
            navArgument(workoutIdArg) { type = NavType.LongType }
        )
    ) {

        WorkoutDiaryRoute(
            onBackClick = onBackClick,
            navigateToExerciseList = navigateToExerciseList,
            navigateToCopyWorkout = navigateToCopyWorkout
        )

    }
}