package com.example.gymtracker.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultTimerServiceRepository @Inject constructor(): TimerServiceRepository {

    /* Bool to check if the timer is either running or not */
    override var currentTimerState: Boolean = false
    override var currentTimerValue: Long = 0

    override val timerTick: Flow<Long> = flow {
        while(true){
            emit(currentTimerValue)
            delay(500) // delay for 500 ms
        }
    }

    override val isTimerRunning: Flow<Boolean> = flow{
        while (true){
            emit(currentTimerState)
            delay(500)
        }
    }

    override fun onTimerTick(timerTick: Long) {
        currentTimerValue = timerTick
    }

    override fun onTimerStateChange(timerState: Boolean) {
        currentTimerState = timerState
    }
}