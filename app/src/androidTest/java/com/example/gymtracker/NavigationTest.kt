package com.example.gymtracker

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.gymtracker.ui.workoutSummary.navigation.workoutScreenSummaryRoute
import com.example.gymtracker.utils.assertCurrentRouteName
import com.example.gymtracker.utils.onNodeWithContentDescription
import com.example.gymtracker.utils.onNodeWithStringId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    private lateinit var navController: TestNavHostController


    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            GymTrackerNavHost(navController = navController)
        }

    }

    @Test
    fun gymTrackerNavHost_verifyStartDestination() {
        navController.assertCurrentRouteName(workoutScreenSummaryRoute)
    }

    @Test
    fun gymTrackerNavHost_clickCreateWorkout_navigateToWorkoutDiaryScreen() {

        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()

        composeTestRule.waitForIdle()

        navController.assertCurrentRouteName("workoutDiaryRoute/{workoutId}")

        /* Navitate back to summary screen */
        composeTestRule.onNodeWithContentDescription(R.string.back_button_cd).performClick()

        navController.assertCurrentRouteName(workoutScreenSummaryRoute)
    }

    @Test
    fun gymTrackerNavHost_navigateToWorkoutExerciseListScreen(){

        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()

        composeTestRule.waitForIdle()

        navController.assertCurrentRouteName("workoutDiaryRoute/{workoutId}")

        composeTestRule.onNodeWithStringId(R.string.add_exercise_button).performClick()

        navController.assertCurrentRouteName("workoutExerciseListRoute/{workoutId}")

        composeTestRule.onNodeWithContentDescription(R.string.back_button_cd).performClick()

        navController.assertCurrentRouteName("workoutDiaryRoute/{workoutId}")

        composeTestRule.onNodeWithContentDescription(R.string.back_button_cd).performClick()

        navController.assertCurrentRouteName(workoutScreenSummaryRoute)


    }

    @Test
    fun gymTrackerNavHost_navigateToCopyWorkoutScreen(){

        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()

        composeTestRule.waitForIdle()

        navController.assertCurrentRouteName("workoutDiaryRoute/{workoutId}")

        composeTestRule.onNodeWithContentDescription(R.string.copy_workout_button_sr).performClick()

        navController.assertCurrentRouteName("workoutCopyRoute/{workoutId}")

    }

}
