package com.example.gymtracker.data.repository

import kotlinx.coroutines.flow.Flow

interface TimerServiceRepository {

    val timerTick: Flow<Long>;
    val isTimerRunning: Flow<Boolean>;

    var currentTimerValue: Long;
    var currentTimerState: Boolean;

    fun onTimerTick(timerTick:Long);
    fun onTimerStateChange(timerState: Boolean);

}