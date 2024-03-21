package com.example.gymtracker.ui.utils

/* Function to calculate the normalized value between min and max value */
fun normalizeValue(value: Float, maxValue: Float, minValue: Float): Float {
    return ((value - minValue) / (maxValue - minValue))
}

/* Function to calculate the desired interval between two values */
fun calculateAxisInterval(maxValue: Float, minValue: Float, numIntervals: Int): List<Float>{

    val intervalList = mutableListOf<Float>()
    val interval = maxValue.minus(minValue).div(numIntervals - 1)

    for( i in 0 .. numIntervals){

        intervalList.add(minValue.plus(i.times(interval)))

    }

    return intervalList

}