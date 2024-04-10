package com.example.gymtracker.ui.commonComposables


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.R
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.utils.calculateAxisInterval
import com.example.gymtracker.ui.utils.calculateNearestPoint
import com.example.gymtracker.ui.utils.normalizeValue
import com.example.gymtracker.ui.utils.toCoordinates
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: ImmutableMap<Float, Float>,
    xAxisFormatter: (Int) -> String = { value -> value.toString() },
    yAxisFormatter: (Int) -> String = { value -> value.toString() }
) {

    val color = MaterialTheme.colorScheme.primary

    var selectedWeightCoordinate by rememberSaveable {
        mutableStateOf(Pair(0f, 0f))
    }

    var weight by rememberSaveable {
        mutableStateOf(0f)
    }

    var tapGestureOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }

    /* Text properties */
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.Gray,
    )
    val graphTextStyle = TextStyle(
        fontSize = 12.sp,
        color = color
    )

    val selectedWeightSr =
        stringResource(id = R.string.exercise_history_chart_selected_weight_sr, weight)
    val noDataSr = stringResource(id = R.string.line_chart_no_data)

    Box(modifier = modifier) {


        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        tapGestureOffset = it
                    }
                }
        ) {

            val numIntervals = 6

            // If data is empty draw text saying no data and return.
            if (data.isEmpty()) {

                val textSize = textMeasurer.measure(noDataSr, graphTextStyle)

                drawText(
                    textMeasurer = textMeasurer,
                    text = "No data available",
                    topLeft = Offset(
                        x = size.width.div(2).minus(textSize.size.width.div(2)),
                        y = size.height.div(2)
                    ),
                    style = graphTextStyle
                )

                return@Canvas
            }

            val padding = 125f

            val xAxisMaxAmplitude = size.width.minus(padding.times(2))
            val yAxisMaxAmplitude = size.height.minus(padding.times(2))

            /* In order to avoid unexpected errors if the size of the data is smaller than the size of
            * the interval just draw the data itself no interval calculation needed. */

            val xAxisInterval = if (data.size < numIntervals) {
                data.keys.map { it.roundToInt() }
            } else {
                calculateAxisInterval(data.keys.max(), data.keys.min(), numIntervals)
            }
            val yAxisInterval = if(data.size < numIntervals){
                data.values.map { it.roundToInt() }
            } else {
                calculateAxisInterval(data.values.max(), data.values.min(), numIntervals)
            }

            // Create the path and move it to the origin of coordinates
            val dotsPath = Path()
            dotsPath.moveTo(padding, yAxisMaxAmplitude.plus(padding))

            drawXAxisGrid(
                xAxisMaxAmplitude,
                xAxisInterval,
                padding,
                textMeasurer,
                textStyle,
                xAxisFormatter
            )
            drawYAxisGrid(
                yAxisMaxAmplitude,
                yAxisInterval,
                padding,
                textMeasurer,
                textStyle,
                yAxisFormatter
            )

            drawAxis(padding)

            val coordinates = data.toCoordinates(
                xAxisMaxAmplitude = xAxisMaxAmplitude,
                xAxisMax = xAxisInterval.max().toFloat(),
                xAxisMin = xAxisInterval.min().toFloat(),
                yAxisMaxAmplitude = yAxisMaxAmplitude,
                yAxisMax = yAxisInterval.max().toFloat(),
                yAxisMin = yAxisInterval.min().toFloat(),
                padding = padding
            )

            /*xTapPosition = coordinates.keys.first()
            yTapPosition = coordinates.values.first()*/

            coordinates.forEach { value ->

                // Move the path to each point
                dotsPath.lineTo(value.key, value.value)

                drawCircle(
                    color = Color.Black,
                    center = Offset(
                        x = value.key,
                        y = value.value,
                    ),
                    radius = 5f
                )

            }

            // Draw the path connecting each point
            drawPath(path = dotsPath, color = Color.Black, style = Stroke(width = 2f))

            selectedWeightCoordinate =
                tapGestureOffset.calculateNearestPoint(coordinates = coordinates)

            /* If there is a valid weight selected draw a text on top of the point with the selected weight */
            if (selectedWeightCoordinate != Pair(0f, 0f)) {

                val coordinatesPosition = coordinates.keys.indexOf(selectedWeightCoordinate.first)
                weight = data.values.elementAt(coordinatesPosition)


                // Text data
                val textSize = textMeasurer.measure(selectedWeightSr, graphTextStyle)

                drawCircle(
                    color = color,
                    center = Offset(
                        x = selectedWeightCoordinate.first,
                        y = selectedWeightCoordinate.second
                    ),
                    radius = 5f
                )

                /* TextBox and text */

                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(
                        x = selectedWeightCoordinate.first.minus(textSize.size.width.div(2))
                            .minus(5f),
                        y = selectedWeightCoordinate.second.minus(textSize.size.height.times(1.2f))
                            .minus(5f)
                    ),
                    size = Size(
                        width = textSize.size.width.toFloat().plus(10f),
                        height = textSize.size.height.toFloat().plus(10f)
                    ),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    color = color,
                    topLeft = Offset(
                        x = selectedWeightCoordinate.first.minus(textSize.size.width.div(2))
                            .minus(5f),
                        y = selectedWeightCoordinate.second.minus(textSize.size.height.times(1.2f))
                            .minus(5f)
                    ),
                    size = Size(
                        width = textSize.size.width.toFloat().plus(10f),
                        height = textSize.size.height.toFloat().plus(10f)
                    ),
                    style = Stroke(width = 3f),
                    cornerRadius = CornerRadius(10f, 10f)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = selectedWeightSr,
                    topLeft = Offset(
                        x = selectedWeightCoordinate.first.minus(textSize.size.width.div(2)),
                        y = selectedWeightCoordinate.second.minus(textSize.size.height.times(1.2f))
                    ),
                    style = graphTextStyle
                )

            }

        }

    }

}

// Axis have to go from min to max
fun DrawScope.drawAxis(padding: Float) {

    // Y axis
    drawLine(
        start = Offset(x = padding, y = size.height.minus(padding)),
        end = Offset(x = padding, y = padding),
        color = Color.Black,
        strokeWidth = 3f
    )

    // X axis
    drawLine(
        start = Offset(x = padding, y = size.height.minus(padding)),
        end = Offset(x = size.width.minus(padding), y = size.height.minus(padding)),
        color = Color.Black,
        strokeWidth = 3f
    )

}

fun DrawScope.drawXAxisGrid(
    xAxisMaxAmplitude: Float,
    xAxisIntervals: List<Int>,
    padding: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    xAxisFormatter: (Int) -> String = { value -> value.toString() }
) {


    xAxisIntervals.forEach { value ->

        val formattedValue = xAxisFormatter(value)
        val textSize = textMeasurer.measure(formattedValue, textStyle)

        drawText(
            textMeasurer = textMeasurer,
            text = formattedValue,
            style = textStyle,
            topLeft = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(
                        value.toFloat(),
                        xAxisIntervals.max().toFloat(),
                        xAxisIntervals.min().toFloat()
                    )
                ).plus(padding).minus(textSize.size.width.times(0.5f)),
                y = size.height.minus(padding).plus(textSize.size.height.times(0.3f))
            )
        )


        drawLine(
            start = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(
                        value.toFloat(),
                        xAxisIntervals.max().toFloat(),
                        xAxisIntervals.min().toFloat()
                    )
                ).plus(padding),
                y = size.height.minus(padding)
            ),
            end = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(
                        value.toFloat(),
                        xAxisIntervals.max().toFloat(),
                        xAxisIntervals.min().toFloat()
                    )
                ).plus(padding),
                y = padding
            ),
            color = Color.LightGray,
            strokeWidth = 3f
        )

    }

}

fun DrawScope.drawYAxisGrid(
    yAxisMaxAmplitude: Float,
    yAxisIntervals: List<Int>,
    padding: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    yAxisFormatter: (Int) -> String = { value -> value.toString() }
) {

    yAxisIntervals.forEach { value ->

        val formattedValue = yAxisFormatter(value)
        val textSize = textMeasurer.measure(value.toString(), textStyle)

        drawText(
            textMeasurer = textMeasurer,
            text = formattedValue,
            style = textStyle,
            topLeft = Offset(
                x = padding.minus(textSize.size.width.times(1.2f)),
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value.toFloat(),
                            yAxisIntervals.max().toFloat(),
                            yAxisIntervals.min().toFloat()
                        )
                    )
                ).plus(padding).minus(textSize.size.height.times(0.5f))
            )
        )

        drawLine(
            start = Offset(
                x = padding,
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value.toFloat(),
                            yAxisIntervals.max().toFloat(),
                            yAxisIntervals.min().toFloat()
                        )
                    )
                ).plus(padding)
            ),
            end = Offset(
                x = size.width.minus(padding),
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value.toFloat(),
                            yAxisIntervals.max().toFloat(),
                            yAxisIntervals.min().toFloat()
                        )
                    )
                ).plus(padding)
            ),
            color = Color.LightGray,
            strokeWidth = 3f
        )

    }

}


@Preview
@Composable
fun LineChartPreview() {
    GymTrackerTheme {

        val data = mutableMapOf(
            LocalDate.now().toEpochDay().toFloat() to 55f,
            LocalDate.now().plusDays(1).toEpochDay().toFloat() to 72.5f,
            LocalDate.now().plusDays(2).toEpochDay().toFloat() to 88.5f,
            LocalDate.now().plusDays(3).toEpochDay().toFloat() to 99.8f,
            LocalDate.now().plusDays(4).toEpochDay().toFloat() to 105.6f,
            LocalDate.now().plusDays(5).toEpochDay().toFloat() to 88.76f,
            LocalDate.now().plusDays(6).toEpochDay().toFloat() to 124.56f,
            LocalDate.now().plusDays(7).toEpochDay().toFloat() to 156.78f,
            LocalDate.now().plusDays(8).toEpochDay().toFloat() to 169.02f,
            LocalDate.now().plusDays(9).toEpochDay().toFloat() to 25.56f,
            LocalDate.now().plusDays(10).toEpochDay().toFloat() to 47.8f,
            LocalDate.now().plusDays(11).toEpochDay().toFloat() to 156.98f,
            LocalDate.now().plusDays(12).toEpochDay().toFloat() to 33.45f,
            LocalDate.now().plusDays(13).toEpochDay().toFloat() to 23.55f,
            LocalDate.now().plusDays(14).toEpochDay().toFloat() to 156.98f,
            LocalDate.now().plusDays(15).toEpochDay().toFloat() to 200.89f,
            LocalDate.now().plusDays(20).toEpochDay().toFloat() to 300.5f
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = data.toPersistentMap(),
            xAxisFormatter = {
                LocalDate.ofEpochDay(it.toLong()).format(DateTimeFormatter.ofPattern("dd/LLL"))
            }
        )
    }
}

@Preview
@Composable
fun DataSizeSmallerThanIntervalSizeLineChartPreview() {
    GymTrackerTheme {

        val data = mutableMapOf(
            LocalDate.now().toEpochDay().toFloat() to 55f,
           /* LocalDate.now().plusDays(1).toEpochDay().toFloat() to 72.5f,
            LocalDate.now().plusDays(2).toEpochDay().toFloat() to 88.5f,*/
        )
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = data.toPersistentMap(),
            xAxisFormatter = {
                LocalDate.ofEpochDay(it.toLong()).format(DateTimeFormatter.ofPattern("dd/LLL"))
            }
        )
    }
}

@Preview
@Composable
fun NoDataLineChartPreview() {
    GymTrackerTheme {

        val data = emptyMap<Float, Float>()

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = data.toPersistentMap(),
            xAxisFormatter = {
                LocalDate.ofEpochDay(it.toLong()).format(DateTimeFormatter.ofPattern("dd/LLL"))
            }
        )
    }
}