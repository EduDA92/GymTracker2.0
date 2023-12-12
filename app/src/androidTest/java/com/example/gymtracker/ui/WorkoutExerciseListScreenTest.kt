package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.example.gymtracker.R
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListScreen
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListScreenState
import com.example.gymtracker.ui.workoutExerciseList.WorkoutExerciseListUiState
import com.example.gymtracker.utils.onNodeWithContentDescription
import com.example.gymtracker.utils.onNodeWithStringId
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test

class WorkoutExerciseListScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun workoutExerciseListScreen_whenLoading_showsLoadingComposable() {

        composeTestRule.setContent {
            WorkoutExerciseListScreen(workoutExerciseListState = WorkoutExerciseListUiState.Loading)
        }

        composeTestRule.onNodeWithStringId(R.string.loading_data_sr).assertExists()

    }

    @Test
    fun workoutExerciseListScreen_whenClickFilterButton_showsFilterBar() {

        composeTestRule.setContent {
            WorkoutExerciseListScreen(
                workoutExerciseListState = WorkoutExerciseListUiState.Success(
                    workoutExerciseListState
                )
            )
        }

        // Open filter menu
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_filter_button_cd)
            .assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_filter_button_cd)
            .performClick()
        // Check if filter chips with ExerciseTYpe are shown
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).assertExists()

        // Close Menu and check
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_filter_button_cd)
            .performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).assertDoesNotExist()
    }

    @Test
    fun workoutExerciseListScreen_filterBar_retainsState() {

        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            WorkoutExerciseListScreen(
                workoutExerciseListState = WorkoutExerciseListUiState.Success(
                    workoutExerciseListState
                )
            )
        }

        // Click filter menu because text gets saved instantly to viewModel so will retain state always
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_filter_button_cd)
            .performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).assertExists()

        // trigger state restoration and check if the filter is still open
        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithText(ExerciseType.Legs.name).assertExists()


    }

    @Test
    fun workoutExerciseListScreen_whenCreatingExercise_showsModalBottomSheet() {

        composeTestRule.setContent {
            WorkoutExerciseListScreen(
                workoutExerciseListState = WorkoutExerciseListUiState.Success(
                    workoutExerciseListState
                )
            )
        }

        // Click on create exercise should trigger modal bottom sheet
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr)
            .assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr)
            .performClick()

        // Check bottom sheet texts
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_name_sr)
            .assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_type_sr)
            .assertExists()

        // Close bottom sheet and check
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_cancel_exercise_creation_text_sr)
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_name_sr)
            .assertDoesNotExist()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_type_sr)
            .assertDoesNotExist()


    }

    @Test
    fun workoutExerciseListScreen_insertingValidData_changeSaveExerciseButtonState() {

        composeTestRule.setContent {
            WorkoutExerciseListScreen(
                workoutExerciseListState = WorkoutExerciseListUiState.Success(
                    workoutExerciseListState
                )
            )
        }

        // Show bottom sheet and check the save exercise button is disabled
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr)
            .performClick()

        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr)
            .assertIsNotEnabled()

        // Fill the fields and check if the button is now enabled
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_exercise_name_text_field_cd)
            .performTextInput("Squat")
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_drop_menu_button_cd)
            .performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).performClick()


        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr)
            .assertIsEnabled()

    }

    @Test
    fun workoutExerciseListScreen_createExerciseBottomSheet_retainsState() {

        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            WorkoutExerciseListScreen(
                workoutExerciseListState = WorkoutExerciseListUiState.Success(
                    workoutExerciseListState
                )
            )
        }
        // Open bottom sheet
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr)
            .performClick()

        // Check bottom is open
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_name_sr)
            .assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_type_sr)
            .assertExists()

        // Type text inside the fields and enable button
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_exercise_name_text_field_cd)
            .performTextInput("Squat")
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_drop_menu_button_cd)
            .performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).performClick()

        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr)
            .assertIsEnabled()

        // trigger state restoration and check
        restorationTester.emulateSavedInstanceStateRestore()


        // Check bottom sheet still open and button enabled
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_name_sr).assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_exercise_type_sr).assertExists()
        composeTestRule.onNodeWithText("Squat").assertExists()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr).assertIsEnabled()


    }


    private val workoutExerciseListState = WorkoutExerciseListScreenState(
        exerciseList = persistentListOf(),
        exerciseTypeFilter = "",
        exerciseNameFilter = ""
    )

}