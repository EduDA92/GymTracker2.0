package com.example.gymtracker.repository

import com.example.gymtracker.data.repository.DefaultTimerServiceRepository
import com.example.gymtracker.utils.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RestTimerRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var timerServiceRepository: DefaultTimerServiceRepository

    @Before
    fun setup(){

        timerServiceRepository = DefaultTimerServiceRepository()

    }

    @Test
    fun defaultRestTimerRepository_onTimerTick_updatesFlowAccordingly() = runTest {

        // Check initial state
        var currentTimerValue = timerServiceRepository.timerTick.first()

        assertEquals(0, currentTimerValue)

        // Update state
        timerServiceRepository.onTimerTick(1000)

        currentTimerValue = timerServiceRepository.timerTick.first()

        assertEquals(1000, currentTimerValue)

    }

    @Test
    fun defaultRestTimerRepository_onTimerStateChange_updatesFlowAccordingly() = runTest {

        // Check initial state
        var currentTimerStateValue = timerServiceRepository.isTimerRunning.first()

        assertEquals(false, currentTimerStateValue)

        // Update state
        timerServiceRepository.onTimerStateChange(true)

        currentTimerStateValue = timerServiceRepository.isTimerRunning.first()

        assertEquals(true, currentTimerStateValue)

    }

}