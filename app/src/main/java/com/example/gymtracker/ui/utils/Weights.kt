package com.example.gymtracker.ui.utils

import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate

object Weights {
    val plates = listOf(
        Plate(weight = 25f, isSelected = true),
        Plate(weight = 20f, isSelected = true),
        Plate(weight = 15f, isSelected = true),
        Plate(weight = 10f, isSelected = true),
        Plate(weight = 5f, isSelected = true),
        Plate(weight = 2.5f, isSelected = true),
        Plate(weight = 1.25f, isSelected = true)
    )

    val bars = listOf(
        Bar(weight = 20f, isSelected = true),
        Bar(weight = 15f, isSelected = false),
        Bar(weight = 10f, isSelected = false)
    )
}