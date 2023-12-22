package com.example.gymtracker



import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.example.gymtracker.ui.model.ExerciseType
import com.example.gymtracker.utils.onNodeWithContentDescription
import com.example.gymtracker.utils.onNodeWithStringId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltAndroidTest
class IntegrationTests {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun gymTracker_basic_behaviour_test()  {

        /* Create workout and assert navigation to diary was done */
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()

        composeTestRule.onNodeWithText("default").assertExists()

        /* Try to change the title and assert was changed */
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextInput("Test")
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performImeAction()
        composeTestRule.onNodeWithText("Test").assertExists()


        composeTestRule.onNodeWithContentDescription(R.string.back_button_cd).performClick()

        composeTestRule.onNodeWithContentDescription(R.string.card_options_button_sr).performClick()
        composeTestRule.onNodeWithStringId(R.string.dropdown_menu_delete_workout_sr).performClick()

        composeTestRule.onNodeWithStringId(R.string.no_workout_data_sr).assertExists()

    }

    /* This test the path of creating workout - creating exercise and adding it to the workout -
    * adding set and modifying it and back to main screen */
    @Test
    fun gymTracker_createExerciseAndAddingSets_test(){

        // Create new workout
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()
        composeTestRule.onNodeWithText("default").assertExists()

        // go to exercise List screen
        composeTestRule.onNodeWithStringId(R.string.add_exercise_button).performClick()

        // Create new exercise
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_exercise_name_text_field_cd).performTextInput("Squat")
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_drop_menu_button_cd).performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr).performClick()

        // tap on exercise and add it to the workout
        composeTestRule.onNode(
            hasText("Squat") and
            hasText("Legs") and
            hasClickAction()
        ).performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_add_exercises_button_sr).assertIsEnabled()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_add_exercises_button_sr).performClick()

        /* add a set to the exercise and check the behaviour:
        * At the start the fields are editable and the icon is an incomplete icon button
        * When the set is submitted the fields are plain text and the icon button is complete icon button
        * When the set is submitted tapping the complete set icon button will do nothing
        * In order to edit a set clicking the text will make the set editable again*/

        composeTestRule.onNodeWithStringId(R.string.add_set_button_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).performTextInput("234")
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).performTextInput("23")
        composeTestRule.onNodeWithContentDescription(R.string.incomplete_exercise_set_button_cd).performClick()

        //check that the fields ain't editable and the button is completed set button
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(R.string.complete_exercise_set_button_cd).assertExists()

        // Check that tapping completed button doesn't set fields to editable
        composeTestRule.onNodeWithContentDescription(R.string.complete_exercise_set_button_cd).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertDoesNotExist()

        // assert that taping in a text makes the field editable again
        composeTestRule.onNodeWithText("23").performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_weight_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_diary_reps_edit_text_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.incomplete_exercise_set_button_cd).assertExists()


    }

    /* This test checks the copy workout function, first will create a workout and an exercise then add the exercise
    * to the workout.
    * Then a new workout will be created an the exercises from the previous workout copied. */
    @Test
    fun gymTracker_copyWorkout_test(){
        val pattern = "EEEE, MMMM d, yyyy"
        val format = DateTimeFormatter.ofPattern(pattern)
        val dateString = LocalDate.now().format(format)

        // Create new workout
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()
        composeTestRule.onNodeWithText("default").assertExists()

        // go to exercise List screen
        composeTestRule.onNodeWithStringId(R.string.add_exercise_button).performClick()

        // Create new exercise
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_create_exercise_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_exercise_name_text_field_cd).performTextInput("Squat")
        composeTestRule.onNodeWithContentDescription(R.string.workout_exercise_list_drop_menu_button_cd).performClick()
        composeTestRule.onNodeWithText(ExerciseType.Legs.name).performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_save_exercise_text_sr).performClick()

        // tap on exercise and add it to the workout
        composeTestRule.onNode(
            hasText("Squat") and
                    hasText("Legs") and
                    hasClickAction()
        ).performClick()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_add_exercises_button_sr).assertIsEnabled()
        composeTestRule.onNodeWithStringId(R.string.workout_exercise_list_add_exercises_button_sr).performClick()

        // navigate back
        composeTestRule.onNodeWithContentDescription(R.string.back_button_cd).performClick()

        // Advance one day
        composeTestRule.onNodeWithContentDescription(R.string.next_date_icon_sr).performClick()

        // Create new workout and change title
        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_sr).performClick()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).assertExists()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextClearance()
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performTextInput("NewWorkout")
        composeTestRule.onNodeWithContentDescription(R.string.workout_title_edit_field_cd).performImeAction()
        composeTestRule.onNodeWithText("NewWorkout").assertExists()

        // Go to copy workout
        composeTestRule.onNodeWithContentDescription(R.string.copy_workout_button_sr).performClick()
        composeTestRule.onRoot().printToLog("NodeTree")
        composeTestRule.onNodeWithText(dateString, substring = true).performClick()
        composeTestRule.onNode(
            hasText(composeTestRule.activity.getString(R.string.copy_workout_copy_button_sr)) and
            hasClickAction()
        ).performClick()

        // Assert we are on the diary page and check for the squat exercise
        composeTestRule.onNodeWithText("NewWorkout").assertExists()
        composeTestRule.onNodeWithText("Squat").assertExists()


    }

}