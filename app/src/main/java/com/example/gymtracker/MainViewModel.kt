package com.example.gymtracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.data.repository.UserPreferencesRepository
import com.example.gymtracker.data.repository.WeightsRepository
import com.example.gymtracker.ui.utils.Weights
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val weightsRepository: WeightsRepository
): ViewModel() {

    val userData = userPreferencesRepository.userData

    fun updateFirstTimeLog(){
        viewModelScope.launch {
            userPreferencesRepository.updateUserFirstTimeLog()
        }
    }

    fun insertWeightsData(){

        viewModelScope.launch {
            weightsRepository.insertAllPlates(Weights.plates)
            weightsRepository.insertAllBars(Weights.bars)
        }

    }

}