package com.example.gymtracker.ui.workoutExerciseList.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListRoute


private const val workoutIdArg = "workoutId"

fun NavController.navigateToWorkoutExerciseList(workoutId: Long){

    this.navigate("workoutExerciseListRoute/$workoutId")

}

fun NavGraphBuilder.workoutExerciseListScreen(onBackClick: () -> Unit = {}){

    composable(
        route = "workoutExerciseListRoute/{$workoutIdArg}",
        arguments = listOf(
            navArgument(workoutIdArg) { type = NavType.LongType}
        )
    ) {

        WorkoutExerciseListRoute(onBackClick = onBackClick)
    }

}