package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.gymtracker.ui.workoutDiary.WorkoutDiary
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryScreen
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryUiState
import com.example.gymtracker.utils.onNodeWithStringId
import com.example.gymtracker.R
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
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

    @Test
    fun workoutDiaryScreen_whenSetIsNotCompleted_editTextsAndCorrectIconIsShown(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, workoutNameEditFieldState = true)
        }

        /* Assert Exercise Name is present and the assert edit text and incomplete button are present */
        composeTestRule.onNodeWithText("Squat").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.incomplete_exercise_set_button_cd).assertExists()

    }

    @Test
    fun workoutDiaryScreen_whenSetIsCompleted_TextsAndCorrectIconIsShown(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = completeWorkoutDiaryUiState, workoutNameEditFieldState = true)
        }

        /* Assert Exercise Name is present and the assert texts and complete button are present */
        composeTestRule.onNodeWithText("Squat").assertExists()
        composeTestRule.onNodeWithText("10").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithText("150.0").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(R.string.complete_exercise_set_button_cd).assertExists()

    }

    private val workoutName = "Test Workout"

    private val workoutDiaryUiState = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutDate = LocalDate.now(),
            workoutName = workoutName,
            exercisesWithReps = listOf(
                ExerciseAndSets(
                    exerciseId = 1,
                    exerciseName = "Squat",
                    exerciseType = ExerciseType.Legs,
                    sets = listOf(
                        ExerciseSet(
                            id = 1,
                            exerciseId = 1,
                            reps = 10,
                            weight = 150f,
                            date = LocalDate.now(),
                            isCompleted = false
                        )
                    )
                )
            )
        )
    )

    private val completeWorkoutDiaryUiState = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutDate = LocalDate.now(),
            workoutName = workoutName,
            exercisesWithReps = listOf(
                ExerciseAndSets(
                    exerciseId = 1,
                    exerciseName = "Squat",
                    exerciseType = ExerciseType.Legs,
                    sets = listOf(
                        ExerciseSet(
                            id = 1,
                            exerciseId = 1,
                            reps = 10,
                            weight = 150f,
                            date = LocalDate.now(),
                            isCompleted = true
                        )
                    )
                )
            )
        )
    )

}