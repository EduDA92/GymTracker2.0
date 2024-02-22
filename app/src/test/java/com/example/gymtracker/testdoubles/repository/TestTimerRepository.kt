package com.example.gymtracker.testdoubles.repository

import com.example.gymtracker.data.repository.TimerServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TestTimerRepository : TimerServiceRepository {

    override val timerTick: Flow<Long> = flow { emit(1L) }

    override val isTimerRunning: Flow<Boolean> = flow { emit(false) }

    override var currentTimerValue: Long = 0

    override var currentTimerState: Boolean = false

    override fun onTimerTick(timerTick: Long) {
        currentTimerValue = timerTick
    }

    override fun onTimerStateChange(timerState: Boolean) {
        currentTimerState = timerState
    }
}