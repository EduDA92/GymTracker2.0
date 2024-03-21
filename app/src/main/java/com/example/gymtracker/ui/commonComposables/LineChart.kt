package com.example.gymtracker.ui.commonComposables


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gymtracker.R
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.utils.calculateAxisInterval
import com.example.gymtracker.ui.utils.normalizeValue
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import java.time.LocalDate

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: ImmutableMap<Float, Float>
) {

    Box(modifier = modifier) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.large_db))
        ) {

            if(data.isEmpty()){
                return@Canvas
            }

            val xAxisMaxAmplitude = size.width
            val yAxisMaxAmplitude = size.height
            val xAxisInterval = calculateAxisInterval(data.keys.max(), data.keys.min(), 10)
            val yAxisInterval = calculateAxisInterval(data.values.max(), data.values.min(), 10)

            // Create the path and move it to the origin of coordinates
            val dotsPath = Path()
            dotsPath.moveTo(0f, yAxisMaxAmplitude)

            data.forEach { value ->

                /* The points will be drawn based on the normalized value between [0-1].
               * The X coordinate will be the Width times the normalized value, the higher the value the greater the distance
               * from the start of the canvas.
               * The Y coordinate is calculated a bit different, first calculate the % of max height(like the X axis)
               * and then subtract this from maxHeight again so the higher the value the closest to the top of the canvas*/

                val xCoordinate = xAxisMaxAmplitude.times(
                    normalizeValue(
                        value.key,
                        xAxisInterval.max(),
                        xAxisInterval.min()
                    )
                )
                val yCoordinate = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value.value,
                            yAxisInterval.max(),
                            yAxisInterval.min()
                        )
                    )
                )

                // Move the path to each point
                dotsPath.lineTo(xCoordinate, yCoordinate)

                drawCircle(
                    color = Color.Black,
                    center = Offset(
                        x = xCoordinate,
                        y = yCoordinate,
                    ),
                    radius = 7f
                )


            }

            drawXAxisGrid(xAxisMaxAmplitude, xAxisInterval)
            drawYAxisGrid(yAxisMaxAmplitude, yAxisInterval)

            drawAxis()

            // Draw the path connecting each point
            drawPath(path = dotsPath, color = Color.Black, style = Stroke(width = 3f))

        }

    }

}

// Axis have to go from min to max
fun DrawScope.drawAxis() {

    // Y axis
    drawLine(
        start = Offset(x = 0f, y = size.height),
        end = Offset(x = 0f, y = 0f),
        color = Color.Black,
        strokeWidth = 3f
    )

    // X axis
    drawLine(
        start = Offset(x = 0f, y = size.height),
        end = Offset(x = size.width, y = size.height),
        color = Color.Black,
        strokeWidth = 3f
    )

}

fun DrawScope.drawXAxisGrid(xAxisMaxAmplitude: Float, xAxisIntervals: List<Float>){

    xAxisIntervals.forEach {value ->

        drawLine(
            start = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(value, xAxisIntervals.max(), xAxisIntervals.min())
                ),
                y = size.height
            ),
            end = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(value, xAxisIntervals.max(), xAxisIntervals.min())
                ),
                y = 0f
            ),
            color = Color.LightGray,
            strokeWidth = 3f
        )

    }

}

fun DrawScope.drawYAxisGrid(yAxisMaxAmplitude: Float, yAxisIntervals: List<Float>){

    yAxisIntervals.forEach { value ->

        drawLine(
            start = Offset(
                x = 0f,
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(normalizeValue(value, yAxisIntervals.max(), yAxisIntervals.min()))
                )
            ),
            end = Offset(
                x = size.width,
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(normalizeValue(value, yAxisIntervals.max(), yAxisIntervals.min()))
                )
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

        // TEst data
        val data = mutableMapOf(
            LocalDate.now().minusDays(1).toEpochDay().toFloat() to 0f,
            LocalDate.now().toEpochDay().toFloat() to 1f,
            LocalDate.now().plusDays(1).toEpochDay().toFloat() to 2f,
            LocalDate.now().plusDays(2).toEpochDay().toFloat() to 3f,
            LocalDate.now().plusDays(3).toEpochDay().toFloat() to 4f,
            LocalDate.now().plusDays(4).toEpochDay().toFloat() to 5f,
            LocalDate.now().plusDays(5).toEpochDay().toFloat() to 6f,
            LocalDate.now().plusDays(6).toEpochDay().toFloat() to 7f,
            LocalDate.now().plusDays(7).toEpochDay().toFloat() to 8f,
            LocalDate.now().plusDays(8).toEpochDay().toFloat() to 9f,
            LocalDate.now().plusDays(9).toEpochDay().toFloat() to 10f,
            LocalDate.now().plusDays(30).toEpochDay().toFloat() to 12f,
            LocalDate.now().plusDays(60).toEpochDay().toFloat() to 13f,
            LocalDate.now().plusDays(120).toEpochDay().toFloat() to 15f,
            LocalDate.now().plusDays(240).toEpochDay().toFloat() to 17f,
            LocalDate.now().plusDays(360).toEpochDay().toFloat() to 20f,
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = data.toPersistentMap()
        )
    }
}