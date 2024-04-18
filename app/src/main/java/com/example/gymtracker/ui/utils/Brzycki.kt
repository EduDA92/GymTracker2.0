package com.example.gymtracker.ui.utils

fun brzyckiOneRepMax(weight: Float, reps: Int): Double{
    return weight.div(1.0278.minus(0.0278.times(reps)))
}