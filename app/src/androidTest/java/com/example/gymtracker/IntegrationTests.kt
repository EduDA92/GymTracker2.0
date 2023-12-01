package com.example.gymtracker



import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.example.gymtracker.utils.onNodeWithContentDescription
import com.example.gymtracker.utils.onNodeWithStringId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    fun gymTracker_basic_behaviour_test() = runTest {

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

}