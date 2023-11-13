package com.example.gymtracker.ui.workoutDiary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.gymtracker.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutDiaryViewModel @Inject constructor(
    workoutRepository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(

) {


    val workoutId: Long = savedStateHandle["workoutId"] ?: 0

}