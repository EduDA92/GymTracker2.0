package com.example.gymtracker.ui.utils

fun calculatePlates(plateList: List<Float>, barWeight:Float, weight: Float): List<Float> {

    val calculatedPlates = mutableListOf<Float>()
    val availablePlateList = plateList.sortedDescending().toMutableList()
    // target weight for the plates will be the total target weight minus the bar weight
    val targetWeight = weight.minus(barWeight).div(2.0f)


    while(availablePlateList.isNotEmpty()){

        if(availablePlateList[0] + calculatedPlates.sum() <= targetWeight){

            calculatedPlates.add(availablePlateList[0])

        } else {

            availablePlateList.removeFirst()

        }

    }

    return calculatedPlates
}