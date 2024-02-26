package com.example.gymtracker.ui.utils

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

object CommonRestTimers {
    val restTimers = persistentListOf<Long>(
        30000, // 30s
        60000, // 1 min
        90000, // 1:30 min
        120000, // 2 min
        180000, // 3 min
        300000, // 5 min
    )
}