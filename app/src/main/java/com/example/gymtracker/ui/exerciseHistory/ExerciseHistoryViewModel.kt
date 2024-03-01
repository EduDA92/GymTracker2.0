package com.example.gymtracker.ui.exerciseHistory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.repository.ExerciseSetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    private val exerciseSetRepository: ExerciseSetRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val exerciseId = savedStateHandle["exerciseId"] ?: 0L



}