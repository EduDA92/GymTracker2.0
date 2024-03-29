package com.example.gymtracker.ui.utils

import kotlin.math.roundToInt

/* Function to calculate the normalized value between min and max value */
fun normalizeValue(value: Float, maxValue: Float, minValue: Float): Float {
    return ((value - minValue) / (maxValue - minValue))
}

/* Function to calculate the desired interval between two values */
fun calculateAxisInterval(maxValue: Float, minValue: Float, numIntervals: Int): List<Int>{

    val intervalList = mutableListOf<Int>()
    val interval = maxValue.minus(minValue).div(numIntervals - 1)

    for( i in 0 .. numIntervals){

        intervalList.add(minValue.plus(i.times(interval)).roundToInt())

    }

    return intervalList

}