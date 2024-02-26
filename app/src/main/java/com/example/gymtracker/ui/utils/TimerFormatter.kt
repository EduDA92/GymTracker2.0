package com.example.gymtracker.ui.utils

fun Long.toTimer(): String{

    val min: Int = this.div(1000).div(60).toInt()
    val second: Int = (this.div(1000) % 60).toInt()

    return String.format("%02d:%02d", min, second)

}
