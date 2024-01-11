package com.example.gymtracker.ui.workoutPlateCalculator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
fun PlatesGraph(
    modifier: Modifier = Modifier,
    plateList: ImmutableList<Float>,
    barWeight: Float
) {

    val textMeasurer = rememberTextMeasurer()

    val paddingValue = 12f
    val maxPlateValue = plateList.max()
    val invMaxPlateValue = 1f.div(maxPlateValue)
    val barColor = Color.LightGray
    val plateColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {

        val yAxisCenter = size.height.div(2)
        val maxPlateHeight = size.height.minus(paddingValue.times(2)).times(0.6f)
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
                x = 0 + paddingValue.times(2),
                y = yAxisCenter.minus(barHeight.div(2f))
            )
        )

        var prevX = barStartingSize

        // Draw the bars
        plateList.forEach { plateValue ->

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
                    y = yAxisCenter.plus(plateValue.times(scaleFactor).div(2))
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
fun PlatesGraphPreview() {
    PlatesGraph(
        plateList = persistentListOf(25f, 25f, 20f, 15f, 5f, 2.5f,1.25f),
        barWeight = 20f
    )
}

@Preview
@Composable
fun WorkoutPlateCalculatorScreenPreview() {
    WorkoutPlateCalculatorScreen()
}