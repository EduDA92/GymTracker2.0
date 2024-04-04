package com.example.gymtracker.ui.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt

/* Transform map of values to map of coordinates where key is X and value Y */
/* The points will be drawn based on the normalized value between [0-1].
* The X coordinate will be the Width times the normalized value, the higher the value the greater the distance
* from the start of the canvas.
* The Y coordinate is calculated a bit different, first calculate the % of max height(like the X axis)
* and then subtract this from maxHeight again so the higher the value the closest to the top of the canvas*/
fun Map<Float, Float>.toCoordinates(
    xAxisMaxAmplitude: Float,
    xAxisMax: Float,
    xAxisMin: Float,
    yAxisMaxAmplitude: Float,
    yAxisMax: Float,
    yAxisMin: Float,
    padding: Float
): Map<Float, Float> {

    return this.mapKeys {
        xAxisMaxAmplitude.times(normalizeValue(it.key, xAxisMax, xAxisMin)).plus(padding)
    }.mapValues {
        yAxisMaxAmplitude.minus(
            yAxisMaxAmplitude.times(
                normalizeValue(
                    it.value,
                    yAxisMax,
                    yAxisMin
                )
            )
        ).plus(padding)
    }

}

/* This function search for the nearest point withing the list of coordinates for a certain offset,
* if there is no nearest Point, return 0f 0f Pair */
fun Offset.calculateNearestPoint(coordinates: Map<Float, Float>): Pair<Float, Float> {


    val nearestPoint = coordinates.filter {
        it.key in (this.x - 10f)..(this.x +  10f) &&
                it.value in (this.y -  10f)..(this.y +  10f)
    }

    return nearestPoint.toList().firstOrNull() ?: Pair(0f, 0f)

}

/* Function to calculate the normalized value between min and max value */
fun normalizeValue(value: Float, maxValue: Float, minValue: Float): Float {
    return ((value - minValue) / (maxValue - minValue))
}

/* Function to calculate the desired interval between two values */
fun calculateAxisInterval(maxValue: Float, minValue: Float, numIntervals: Int): List<Int> {

    val intervalList = mutableListOf<Int>()
    val interval = maxValue.minus(minValue).div(numIntervals - 1).roundToInt()

    for (i in 0..numIntervals) {

        intervalList.add(minValue.plus(i.times(interval)).roundToInt())

    }

    return intervalList

}