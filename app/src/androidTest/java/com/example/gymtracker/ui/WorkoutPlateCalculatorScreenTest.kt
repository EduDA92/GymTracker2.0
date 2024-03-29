package com.example.gymtracker.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.gymtracker.R
import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate
import com.example.gymtracker.ui.workoutPlateCalculator.WeightsState
import com.example.gymtracker.ui.workoutPlateCalculator.WorkoutPlateCalculatorScreen
import com.example.gymtracker.ui.workoutPlateCalculator.WorkoutPlateCalculatorUiState
import com.example.gymtracker.utils.onNodeWithContentDescription
import com.example.gymtracker.utils.onNodeWithStringId
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test

class WorkoutPlateCalculatorScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun workoutPlateCalculatorScreen_whenLoadingData_showsLoadingState(){

        composeTestRule.setContent {

            WorkoutPlateCalculatorScreen(state = WorkoutPlateCalculatorUiState.Loading)

        }

        composeTestRule.onNodeWithStringId(R.string.loading_data_sr).assertExists()

    }

    @Test
    fun workoutPlateCalculatorScreen_whenSuccessState_showsDataCorrectly(){

        composeTestRule.setContent {

            WorkoutPlateCalculatorScreen(state = state)

        }

        // First check plate filter bars if are enabler or disable correctly
        composeTestRule.onNode(hasText("20.0") and hasClickAction()).assertIsSelected()
        composeTestRule.onNode(hasText("10.0") and hasClickAction()).assertIsNotSelected()
        composeTestRule.onNode(hasText("5.0") and hasClickAction()).assertIsNotSelected()

        // Check the Bar section
        composeTestRule.onNode(hasText("15.0") and hasClickAction()).assertIsNotSelected()
        composeTestRule.onNode(hasText("12.0") and hasClickAction()).assertIsSelected()
        composeTestRule.onNode(hasText("7.5") and hasClickAction()).assertIsNotSelected()


    }

    @Test
    fun workoutPlateCalculatorScreen_weightInputDialog_behavesCorrectly(){

        composeTestRule.setContent {
            WorkoutPlateCalculatorScreen(state = state)
        }

        //Open the creation plate dialog and check that exist
        composeTestRule.onNodeWithContentDescription(R.string.workout_plate_screen_add_plate_button_cd).performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_plate_calulator_plate_dialog_title).assertExists()

        // Test that when the text is 0 or empty confirm button is disabled
        composeTestRule.onNodeWithStringId(R.string.confirm_sr).assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription(R.string.workout_plate_calulator_dialog_weight_edit_text_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_plate_calulator_dialog_weight_edit_text_cd).performTextInput("0")
        composeTestRule.onNodeWithText("0").assertExists()
        composeTestRule.onNodeWithStringId(R.string.confirm_sr).assertIsNotEnabled()

        // Test that when the text isn't 0 or empty confirm button is enabled
        composeTestRule.onNodeWithContentDescription(R.string.workout_plate_calulator_dialog_weight_edit_text_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_plate_calulator_dialog_weight_edit_text_cd).performTextInput("100.0")
        composeTestRule.onNodeWithText("100.0").assertExists()
        composeTestRule.onNodeWithStringId(R.string.confirm_sr).assertIsEnabled()



    }


    private val state = WorkoutPlateCalculatorUiState.Success(
        WeightsState(
            weight = "300.5",
            barWeight = 20f,
            calculatedPlateList = persistentListOf(25f, 20f, 10f),
            plateList = persistentListOf(
                Plate(1, 20f, true),
                Plate(2, 10f, false),
                Plate(3, 5f, false)
            ),
            barList = persistentListOf(
                Bar(1, 15f, false),
                Bar(2, 12f, true),
                Bar(3, 7.5f, false)
            )
        )
    )

}