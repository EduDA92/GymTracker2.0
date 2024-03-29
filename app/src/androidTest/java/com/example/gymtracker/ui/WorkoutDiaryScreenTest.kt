package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.gymtracker.ui.workoutDiary.WorkoutDiary
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryScreen
import com.example.gymtracker.ui.workoutDiary.WorkoutDiaryUiState
import com.example.gymtracker.utils.onNodeWithStringId
import com.example.gymtracker.R
import com.example.gymtracker.ui.model.ExerciseAndSets
import com.example.gymtracker.ui.model.ExerciseSet
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.workoutDiary.RestTimerDialog
import com.example.gymtracker.ui.workoutDiary.TimerState
import com.example.gymtracker.utils.onNodeWithContentDescription
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class WorkoutDiaryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun workoutDiaryScreen_loadingState_showsLoading(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = WorkoutDiaryUiState.Loading, timerState = timerState)
        }

        composeTestRule.onNodeWithStringId(R.string.loading_data_sr)

    }

    @Test
    fun workoutDiaryScreen_whenWorkoutEditFieldStateIsFalse_TitleWithEditIconIsPresent(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, timerState = timerState)
        }

        /* By default state is false so check for the WorkoutName set in the testState and the content Description of the edit icon */
        composeTestRule.onNodeWithText(workoutName).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).assertExists()

    }

    @Test
    fun workoutDiaryScreen_whenWorkoutEditFieldStateIsTrue_editTextFieldIsPresent(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, timerState = timerState)
        }

        // Click button to make edit field appear
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).assertExists()

    }

    @Test
    fun workoutDiaryScreen_whenRestoringState_editTextFieldsRetainState(){

        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, timerState = timerState)
        }

        // Clean edittext title, reps and weight
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).performClick() // Open title edit field
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).performTextClearance()

        // Add new text
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextInput("NewWorkout")
        composeTestRule.onNodeWithText("NewWorkout").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).performTextInput("234")
        composeTestRule.onNodeWithText("234").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).performTextInput("23")
        composeTestRule.onNodeWithText("23").assertExists()

        // trigger recreation and check
        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText("NewWorkout").assertExists()
        composeTestRule.onNodeWithText("234").assertExists()
        composeTestRule.onNodeWithText("23").assertExists()

    }

    @Test
    fun workoutDiaryScreen_whenSetIsNotCompleted_editTextsAndCorrectIconIsShown(){

        composeTestRule.setContent {
            WorkoutDiaryScreen(workoutDiaryUiState = workoutDiaryUiState, timerState = timerState)
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
            WorkoutDiaryScreen(workoutDiaryUiState = completeWorkoutDiaryUiState, timerState = timerState)
        }

        /* Assert Exercise Name is present and the assert texts and complete button are present */
        composeTestRule.onNodeWithText("Squat").assertExists()
        composeTestRule.onNodeWithText("10").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithText("150.0").assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(R.string.complete_exercise_set_button_cd).assertExists()

    }

    @Test
    fun workoutDiaryScreen_TimerNotActiveState_ShowsCorrectLayout(){

        composeTestRule.setContent {
            RestTimerDialog(timerState = timerState, commonTimers = persistentListOf(60000), workoutId = 1)
        }

        /* Chech for incative timer layout*/
        composeTestRule.onNodeWithStringId(R.string.rest_timer_dialog_common_timers_title).assertExists()

        // check for 01:00 min text
        composeTestRule.onNodeWithText("01:00").assertExists()

        // check for 60s default text
        composeTestRule.onNodeWithText("60").assertExists()

        composeTestRule.onNodeWithStringId(R.string.rest_timer_dialog_start_timer_sr).assertExists()

    }

    @Test
    fun workoutDiaryScreen_TimerActiveState_ShowsCorrectLayout(){
        composeTestRule.setContent {
            RestTimerDialog(timerState = timerState.copy(timerState = true), commonTimers = persistentListOf(60000), workoutId = 1)
        }

        // CHeck for cancel timer button
        composeTestRule.onNodeWithStringId(R.string.rest_timer_dialog_cancel_timer_sr).assertExists()

    }

    private val workoutName = "Test Workout"

    private val workoutDiaryUiState = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutDate = LocalDate.now(),
            workoutName = workoutName,
            exercisesWithReps = persistentListOf(
                ExerciseAndSets(
                    exerciseId = 1,
                    exerciseName = "Squat",
                    exerciseType = ExerciseType.Legs,
                    sets = persistentListOf(
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

    private val timerState = TimerState(0L, 0,false)

    private val completeWorkoutDiaryUiState = WorkoutDiaryUiState.Success(
        WorkoutDiary(
            workoutId = 1,
            workoutDate = LocalDate.now(),
            workoutName = workoutName,
            exercisesWithReps = persistentListOf(
                ExerciseAndSets(
                    exerciseId = 1,
                    exerciseName = "Squat",
                    exerciseType = ExerciseType.Legs,
                    sets = persistentListOf(
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