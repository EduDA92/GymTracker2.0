package com.example.gymtracker.ui.workoutPlateCalculator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gymtracker.R
import com.example.gymtracker.ui.commonComposables.LoadingState
import com.example.gymtracker.ui.model.Bar
import com.example.gymtracker.ui.model.Plate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun WorkoutPlateCalculatorRoute(
    modifier: Modifier = Modifier,
    viewModel: WorkoutPlateCalculatorViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {

    val state by viewModel.weightState.collectAsStateWithLifecycle()

    WorkoutPlateCalculatorScreen(
        modifier = modifier,
        state = state,
        updateWeight = viewModel::updateWeight,
        onBackClick = onBackClick
    )

}

@Composable
fun WorkoutPlateCalculatorScreen(
    modifier: Modifier = Modifier,
    state: WorkoutPlateCalculatorUiState,
    updateWeight: (String) -> Unit = {},
    updatePlateSelectedState: (Long, Boolean) -> Unit = { _, _ -> },
    updateBarSelectedState: (Long, Boolean) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {

    when (state) {
        WorkoutPlateCalculatorUiState.Loading -> LoadingState()
        is WorkoutPlateCalculatorUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                WorkoutPlateCalculatorTopAppBar(onBackClick = onBackClick)
                TargetWeight(onWeightChangeDone = updateWeight)
                AvailablePlates(
                    plates = state.weightState.plateList,
                    updatePlateSelectedState = updatePlateSelectedState
                )
                AvailableBars(bars = state.weightState.barList)
                PlatesGraph(
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_dp)),
                    plateList = state.weightState.calculatedPlateList,
                    barWeight = state.weightState.barWeight
                )
            }
        }
    }


}

@Composable
fun PlatesGraph(
    modifier: Modifier = Modifier,
    plateList: ImmutableList<Float>,
    barWeight: Float
) {

    val textMeasurer = rememberTextMeasurer()
    val errorStringResource = stringResource(R.string.workout_plate_screen_error_text)
    val noPlatesToDisplayStringResource =
        stringResource(id = R.string.workout_plate_screen_no_plates_text)
    val barColor = Color.LightGray
    val plateColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {

        // Check if there are plates to display, if not return.
        if (plateList.isEmpty()) {
            drawText(
                textMeasurer = textMeasurer,
                text = noPlatesToDisplayStringResource,
                topLeft = Offset(
                    x = size.width.div(2f).minus(330f),
                    y = size.height.div(2f)
                ),
                style = TextStyle(
                    color = Color.Red
                )
            )
            return@Canvas
        }

        val paddingValue = 12f
        val maxPlateValue = plateList.max()
        val invMaxPlateValue = 1f.div(maxPlateValue)

        val yAxisCenter = size.height.div(2)
        val maxPlateHeight = size.height.minus(paddingValue.times(2)).times(0.8f)
        val scaleFactor = invMaxPlateValue.times(maxPlateHeight)

        // Fixed sizes for the bar, plate separation and plate width
        val barHeight = size.height.div(40)
        val barStartingSize = size.width.div(10)
        val plateSeparation = size.width.div(40)
        val plateWidth = size.width.div(20)

        // val rectangles radius
        val cornerRadius = CornerRadius(10f, 10f)


        // First draw the bar plus a text with the bar weight inside
        drawRoundRect(
            color = barColor,
            topLeft = Offset(
                x = 0f + paddingValue,
                y = yAxisCenter.minus(barHeight.div(2f))
            ),
            size = Size(width = barStartingSize, height = barHeight),
            cornerRadius = cornerRadius
        )
        drawText(
            textMeasurer = textMeasurer,
            text = barWeight.toString(),
            topLeft = Offset(
                x = 0 + paddingValue,
                y = yAxisCenter.plus(barHeight.div(2f))
            )
        )

        var prevX = barStartingSize

        // Draw the bars
        plateList.forEach { plateValue ->

            if (prevX > size.width) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = errorStringResource,
                    topLeft = Offset(
                        x = size.width.div(2).minus(150f),
                        y = 0f
                    ),
                    style = TextStyle(
                        color = Color.Red
                    )
                )
            } else {
                // Plate
                drawRoundRect(
                    color = plateColor,
                    topLeft = Offset(
                        x = prevX,
                        y = yAxisCenter.minus(plateValue.times(scaleFactor).div(2f))
                    ),
                    size = Size(width = plateWidth, height = plateValue.times(scaleFactor)),
                    cornerRadius
                )

                // Plate text
                drawText(
                    textMeasurer = textMeasurer,
                    text = plateValue.toString(),
                    topLeft = Offset(
                        x = prevX,
                        y = yAxisCenter.plus(plateValue.times(scaleFactor).div(2f))
                    )
                )

                /*plate separator */
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(
                        x = prevX + plateWidth,
                        y = yAxisCenter.minus(barHeight.div(2f))
                    ),
                    size = Size(width = plateSeparation, height = barHeight),

                    )

                prevX += plateSeparation + plateWidth
            }

        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailablePlates(
    modifier: Modifier = Modifier,
    plates: ImmutableList<Plate>,
    updatePlateSelectedState: (Long, Boolean) -> Unit = { _, _ -> }
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_dp))
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.workout_plate_available_plates_sr),
                fontSize = 18.sp
            )

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {

            plates.forEach { plate ->

                FilterChip(
                    selected = plate.isSelected,
                    onClick = { updatePlateSelectedState(plate.id, !plate.isSelected) },
                    label = { Text(plate.weight.toString()) },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.small_dp))
                )

            }

        }


    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableBars(
    modifier: Modifier = Modifier,
    bars: ImmutableList<Bar>
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_dp))
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.workout_plate_available_bars_sr),
                fontSize = 18.sp
            )

            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }

        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {

            bars.forEach { bar ->

                FilterChip(
                    selected = bar.isSelected,
                    onClick = { /*TODO*/ },
                    label = { Text(text = bar.weight.toString()) },
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.small_dp))
                )

            }

        }

    }

}

@Composable
fun TargetWeight(
    modifier: Modifier = Modifier,
    onWeightChangeDone: (String) -> Unit = {}
) {

    var weight by rememberSaveable {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.medium_dp))
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.workout_plate_calulator_target_weight_sr),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.size(24.dp))

        BasicTextField(
            value = weight,
            onValueChange = { weight = it },
            textStyle = TextStyle(
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .wrapContentHeight(Alignment.CenterVertically)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp)
                ) {

                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if(weight.isNotEmpty()){
                        onWeightChangeDone(weight)
                    }
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .requiredSize(width = 56.dp, height = 46.dp)
                .semantics { contentDescription = "weightEditTextsCd" }
        )

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

    val state = WorkoutPlateCalculatorUiState.Success(
        WeightsState(
            weight = "300.5",
            barWeight = 20f,
            calculatedPlateList = persistentListOf(22.5f, 20f, 10f),
            plateList = persistentListOf(
                Plate(1, 20f, true),
                Plate(2, 10f, false),
                Plate(3, 5f, false),
                Plate(1, 20f, true),
                Plate(2, 10f, false),
                Plate(3, 5f, false),
                Plate(1, 20f, true),
                Plate(2, 10f, false),
                Plate(3, 5f, false)
            ),
            barList = persistentListOf(
                Bar(1, 20f, true),
                Bar(2, 10f, false),
                Bar(3, 5f, false),
                Bar(1, 20f, true),
                Bar(2, 10f, false),
                Bar(3, 5f, false),
                Bar(1, 20f, true),
                Bar(2, 10f, false),
                Bar(3, 5f, false)
            )
        )
    )

    WorkoutPlateCalculatorScreen(state = state)
}

@Preview
@Composable
fun PlatesGraphPreview() {
    PlatesGraph(
        plateList = persistentListOf(25f, 25f, 20f, 15f, 10f, 5f, 2.5f, 1.25f),
        barWeight = 20f
    )
}