package com.example.gymtracker.data.dao.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.gymtracker.data.dao.utils.onNodeWithStringId
import com.example.gymtracker.ui.workourSummary.WorkoutSummaryScreen
import com.example.gymtracker.ui.workourSummary.WorkoutSummaryUiState
import com.example.gymtracker.R
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
    fun workoutSummaryScreen_whenEmptyData_showEmptyData(){
        composeTestRule.setContent {

            WorkoutSummaryScreen(
                workoutSummaryUiState = WorkoutSummaryUiState.EmptyData,
                date = LocalDate.now()
            )

        }

        composeTestRule.onNodeWithStringId(R.string.no_workout_data_sr).assertExists()
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).assertExists()

    }

}