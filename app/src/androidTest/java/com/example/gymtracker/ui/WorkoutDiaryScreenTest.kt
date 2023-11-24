package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.gymtracker.ui.workoutDiary.WorkoutDiary
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryScreen
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryUiState
import com.example.gymtracker.utils.onNodeWithStringId
import com.example.gymtracker.R
import com.example.gymtracker.utils.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class WorkoutDiaryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun workoutDiaryScreen_loadingState_showsLoading(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = WorkoutDiaryUiState.Loading, workoutNameEditFieldState = false)
        }

        composeTestRule.onNodeWithStringId(R.string.loading_data_sr)

    }

    @Test
    fun workoutDiaryScreen_whenWorkoutEditFieldStateIsFalse_TitleWithEditIconIsPresent(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, workoutNameEditFieldState = false)
        }

        /* Check for the WorkoutName set in the testState and the content Description of the edit icon */
        composeTestRule.onNodeWithText(workoutName).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).assertExists()

    }

    @Test
    fun workoutDiaryScreen_whenWorkoutEditFieldStateIsTrue_editTextFieldIsPresent(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, workoutNameEditFieldState = true)
        }

        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).assertExists()

    }

    private val workoutName = "Test Workout"

    private val workoutDiaryUiState = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutDate = LocalDate.now(),
            workoutName = workoutName,
            exercisesWithReps = emptyList()
        )
    )

}