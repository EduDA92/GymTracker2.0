package com.example.gymtracker.ui.workoutPlateCalculator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.R

@Composable
fun WorkoutPlateCalculatorRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {

    WorkoutPlateCalculatorScreen(onBackClick = onBackClick)

}

@Composable
fun WorkoutPlateCalculatorScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {

    Column(modifier = modifier.fillMaxSize()) {
        WorkoutPlateCalculatorTopAppBar(onBackClick = onBackClick)
        TargetWeight(weight = 23)


    }

}

@Composable
fun TargetWeight(
    modifier: Modifier = Modifier,
    weight: Int,
    onWeightChange: (Int) -> Unit = {}
) {

    Surface(modifier = modifier.fillMaxWidth()) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.workout_plate_calulator_target_weight_sr),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = weight.toString(),
                onValueChange = { weight -> onWeightChange(weight.toInt()) },
                modifier = Modifier
                    .weight(3f)
                    .wrapContentWidth(Alignment.Start)
                    .requiredWidth(100.dp)
            )

        }

    }

}

@Composable
fun WorkoutPlateCalculatorTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {

    Box(modifier = modifier.fillMaxWidth()) {

        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = stringResource(id = R.string.back_button_cd)
            )
        }

        Text(
            text = stringResource(id = R.string.workout_plate_calculator_title_sr),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(start = dimensionResource(id = R.dimen.small_dp))
                .align(Alignment.Center)
        )
    }

}

@Preview
@Composable
fun WorkoutPlateCalculatorScreenPreview() {
    WorkoutPlateCalculatorScreen()
}