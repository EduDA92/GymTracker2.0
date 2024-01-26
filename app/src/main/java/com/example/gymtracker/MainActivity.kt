package com.example.gymtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.gymtracker.ui.MainViewModel
import com.example.gymtracker.ui.theme.GymTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user logged in for the first time and insert predefined data
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.userData.collect{

                    if(it.firstTimeLog){
                        viewModel.insertWeightsData()
                        viewModel.updateFirstTimeLog()
                    }

                }

            }
        }

        setContent {
            GymTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GymTrackerNavHost()
                }
            }
        }
    }
}
