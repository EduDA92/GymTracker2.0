package com.example.gymtracker.utilTests

import com.example.gymtracker.ui.utils.calculatePlates
import org.junit.Test
import kotlin.test.assertEquals

class PlateCalculatorTest {

    val plates = setOf(25f,20f,15f,10f,5f,2.5f,1.25f,1f,0.75f,0.5f,0.25f)

    @Test
    fun plateCalculator_weightUnderBarWeight_returnsEmptyList(){

        assertEquals(emptyList(), calculatePlates(plates.toList(), 20.0f, 0f))

    }

    @Test
    fun plateCalculator_weightNumberNotRound_returnApproximateWeightPlates(){

        val calculatedPlates = listOf(25f, 25f, 5f, 2.5f, 1f)

        assertEquals(calculatedPlates, calculatePlates(plates.toList(), 20.0f, 137.369f))

    }

    @Test
    fun plateCalculator_normalWeight_returnsCorrectPlates(){

        val calculatedPlates = listOf(25f, 15f)

        assertEquals(calculatedPlates, calculatePlates(plates.toList(), 20.0f, 100f))

    }

}