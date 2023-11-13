package com.example.gymtracker

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.gymtracker.GymTrackerNavHost
import com.example.gymtracker.MainActivity
import com.example.gymtracker.ui.workourSummary.navigation.workoutScreenSummaryRoute
import com.example.gymtracker.utils.assertCurrentRouteName
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
    fun GymTrackerNavHost_verifyStartDestination() {
        navController.assertCurrentRouteName(workoutScreenSummaryRoute)
    }

    @Test
    fun GymTrackerNavHost_clickCreateWorkout_navigateToWorkoutDiaryScreen() {

        composeTestRule.onNodeWithStringId(R.string.create_workout_button_sr).performClick()

        navController.assertCurrentRouteName("workoutDiaryRoute/{workoutId}")
    }

}
