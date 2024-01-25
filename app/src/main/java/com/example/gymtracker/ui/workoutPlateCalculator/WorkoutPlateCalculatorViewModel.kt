package com.example.gymtracker.ui.workoutPlateCalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.model.PlateEntity
import com.example.gymtracker.data.repository.WeightsRepository
import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate
import com.example.gymtracker.ui.utils.calculatePlates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutPlateCalculatorViewModel @Inject constructor(private val weightsRepository: WeightsRepository) :
    ViewModel() {

    private val _weight = MutableStateFlow("0")

    private val plateList = weightsRepository.observePlates()

    private val barList = weightsRepository.observeBars()

    val weightState: StateFlow<WorkoutPlateCalculatorUiState> =
        combine(_weight, plateList, barList) { weight, plates, bars ->

            val availablePlates = plates.filter { it.isSelected }.map { it.weight }
            val bar = bars.firstOrNull() { it.isSelected }?.weight ?: 20f

            val calculatedPlates = calculatePlates(availablePlates, bar, weight.toFloat())


            WorkoutPlateCalculatorUiState.Success(
                WeightsState(
                    weight = weight,
                    barWeight = bar,
                    calculatedPlateList = calculatedPlates.toImmutableList(),
                    plateList = plates.toImmutableList(),
                    barList = bars.toImmutableList()
                )
            )

        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            WorkoutPlateCalculatorUiState.Loading
        )

    fun updateWeight(weight: String) {

        _weight.update {
            weight
        }

    }

    fun updatePlateSelectedState(plateId: Long, isSelected: Boolean){

        viewModelScope.launch {
            weightsRepository.updatePlateIsSelected(
                plateId = plateId,
                isPlateSelected = isSelected
            )
        }

    }

    /* This one is a bit trickier than the plates case because only one bar can be selected at
    * the same time so we need to change the prev bar selected state and the current selected one,
    * also if the the current selected plate is already selected cannot be deselected just by taping
    * on it. */
    fun updateBarSelectedState(prevBarId: Long, newBarId: Long){

        viewModelScope.launch {

            if(prevBarId != newBarId){

                weightsRepository.updateBarIsSelected(prevBarId, false)
                weightsRepository.updateBarIsSelected(newBarId, true)

            }
        }

    }

    fun createPlate(plateWeight: Float){

        viewModelScope.launch {

            weightsRepository.upsertPlate(
                Plate(
                    weight = plateWeight,
                    isSelected = false
                )
            )

        }

    }

    fun createBar(barWeight: Float){

        viewModelScope.launch {

            weightsRepository.upsertBar(
                Bar(
                    weight = barWeight,
                    isSelected = false
                )
            )

        }

    }




}

sealed interface WorkoutPlateCalculatorUiState {

    data class Success(val weightState: WeightsState) : WorkoutPlateCalculatorUiState
    object Loading : WorkoutPlateCalculatorUiState

}

data class WeightsState(
    val weight: String,
    val barWeight: Float,
    val calculatedPlateList: ImmutableList<Float>,
    val plateList: ImmutableList<Plate>,
    val barList: ImmutableList<Bar>
)