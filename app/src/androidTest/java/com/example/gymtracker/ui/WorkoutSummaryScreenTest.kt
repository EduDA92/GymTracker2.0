package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.gymtracker.utils.onNodeWithStringId
import com.example.gymtracker.ui.workoutSummary.WorkoutSummaryScreen
import com.example.gymtracker.ui.workoutSummary.WorkoutSummaryUiState
import com.example.gymtracker.R
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.workoutSummary.ExerciseSummary
import com.example.gymtracker.ui.workoutSummary.WorkoutSummary
import com.example.gymtracker.utils.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class WorkoutSummaryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun workoutSummaryScreen_whenLoadingData_showsLoading() {

        composeTestRule.setContent {

            WorkoutSummaryScreen(
                workoutSummaryUiState = WorkoutSummaryUiState.Loading,
                date = LocalDate.now()
            )

        }

        composeTestRule.onNodeWithStringId(R.string.loading_data_sr).assertExists()

    }

    @Test
    fun workoutSummaryScreen_whenEmptyData_showEmptyData() {
        composeTestRule.setContent {

            WorkoutSummaryScreen(
                workoutSummaryUiState = WorkoutSummaryUiState.EmptyData,
                date = LocalDate.now()
            )

        }

        composeTestRule.onNodeWithStringId(R.string.no_workout_data_sr).assertExists()
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).assertExists()

    }

    @Test
    fun workoutSummaryScreen_whenOptionsButtonClick_showsMenu() {

        composeTestRule.setContent {
            WorkoutSummaryScreen(
                workoutSummaryUiState = workoutSummary,
                date = LocalDate.now()
            )
        }

        composeTestRule.onNodeWithContentDescription(R.string.card_options_button_sr).performClick()
        composeTestRule.onNodeWithStringId(R.string.dropdown_menu_delete_workout_sr).assertExists()

    }

    @Test
    fun workoutSummaryScreen_whenSuccess_showsData() {

        composeTestRule.setContent {
            WorkoutSummaryScreen(
                workoutSummaryUiState = workoutSummary,
                date = LocalDate.now()
            )
        }

        composeTestRule.onNodeWithStringId(R.string.today_sr).assertExists()

        /* Check card titles */
        composeTestRule.onNodeWithStringId(R.string.workout_screen_today_workout_sr).assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_screen_today_statistics_sr)
            .assertExists()

        /* Check some main card text */
        composeTestRule.onNodeWithText("Push").assertExists()
        composeTestRule.onNodeWithText(workoutSummary.workoutSummary.workoutDate.toString())
            .assertExists()
        composeTestRule.onNodeWithText("Squat").assertExists()
        composeTestRule.onNodeWithText("Deadlift").assertExists()
        composeTestRule.onNodeWithText("Lunge").assertExists()

        /* Total volume card  */
        composeTestRule.onNodeWithStringId(R.string.total_reps_weight_card_reps_title_sr)
            .assertExists()
        composeTestRule.onNodeWithStringId(R.string.total_reps_weight_card_weight_title_sr)
            .assertExists()

    }


    private val workoutSummary = WorkoutSummaryUiState.Success(
        workoutSummary = WorkoutSummary(
            workoutId = 1,
            workoutName = "Push",
            workoutDate = LocalDate.now(),
            workoutTotalWeightVolume = 3000f,
            workoutTotalRepsVolume = 250,
            workoutExerciseDistribution = mutableMapOf(
                ExerciseType.Arms to 2,
                ExerciseType.Chest to 3,
                ExerciseType.Shoulders to 2
            ),
            exercisesSummary = listOf(
                ExerciseSummary(
                    name = "Squat",
                    sets = 300,
                    topSet = Pair(100.75f, 10)
                ),
                ExerciseSummary(
                    name = "Deadlift",
                    sets = 3,
                    topSet = Pair(100f, 8)
                ),
                ExerciseSummary(
                    name = "Lunge",
                    sets = 3,
                    topSet = Pair(100f, 8)
                )
            )
        )
    )

}