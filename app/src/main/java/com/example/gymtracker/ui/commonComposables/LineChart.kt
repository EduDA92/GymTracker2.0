package com.example.gymtracker.ui.commonComposables


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtracker.ui.theme.GymTrackerTheme
import com.example.gymtracker.ui.utils.calculateAxisInterval
import com.example.gymtracker.ui.utils.normalizeValue
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toPersistentMap
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: ImmutableMap<Float, Float>
) {

    /* Text properties */
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        fontSize = 12.sp,
        color = Color.Gray,

    )

    Box(modifier = modifier) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {

            if (data.isEmpty()) {
                return@Canvas
            }

            val padding = 100f

            val xAxisMaxAmplitude = size.width.minus(padding.times(2))
            val yAxisMaxAmplitude = size.height.minus(padding.times(2))
            val xAxisInterval = calculateAxisInterval(data.keys.max(), data.keys.min(), 6)
            val yAxisInterval = calculateAxisInterval(data.values.max(), data.values.min(), 6)

            // Create the path and move it to the origin of coordinates
            val dotsPath = Path()
            dotsPath.moveTo(padding, yAxisMaxAmplitude.plus(padding))

            drawXAxisGrid(xAxisMaxAmplitude, xAxisInterval, padding, textMeasurer, textStyle)
            drawYAxisGrid(yAxisMaxAmplitude, yAxisInterval, padding, textMeasurer, textStyle)

            drawAxis(padding)


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
                ).plus(padding)

                val yCoordinate = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value.value,
                            yAxisInterval.max(),
                            yAxisInterval.min()
                        )
                    )
                ).plus(padding)

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

            // Draw the path connecting each point
            drawPath(path = dotsPath, color = Color.Black, style = Stroke(width = 3f))


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
    xAxisIntervals: List<Float>,
    padding: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {

    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/LLL")


    xAxisIntervals.forEach { value ->

        val textSize = textMeasurer.measure(LocalDate.ofEpochDay(value.toLong()).format(dateTimeFormatter),textStyle)

        drawText(
            textMeasurer = textMeasurer,
            text = LocalDate.ofEpochDay(value.toLong()).format(dateTimeFormatter),
            style = textStyle,
            topLeft = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(value, xAxisIntervals.max(), xAxisIntervals.min())
                ).plus(padding).minus(textSize.size.width.times(0.5f)),
                y = size.height.minus(padding).plus(textSize.size.height.times(0.3f))
            )
        )


        drawLine(
            start = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(value, xAxisIntervals.max(), xAxisIntervals.min())
                ).plus(padding),
                y = size.height.minus(padding)
            ),
            end = Offset(
                x = xAxisMaxAmplitude.times(
                    normalizeValue(value, xAxisIntervals.max(), xAxisIntervals.min())
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
    yAxisIntervals: List<Float>,
    padding: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle
) {

    yAxisIntervals.forEach { value ->

        val textSize = textMeasurer.measure(value.toInt().toString(), textStyle)

        drawText(
            textMeasurer = textMeasurer,
            text = value.toInt().toString(),
            style = textStyle,
            topLeft = Offset(
                x = padding.minus(textSize.size.width.times(1.2f)),
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value,
                            yAxisIntervals.max(),
                            yAxisIntervals.min()
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
                            value,
                            yAxisIntervals.max(),
                            yAxisIntervals.min()
                        )
                    )
                ).plus(padding)
            ),
            end = Offset(
                x = size.width.minus(padding),
                y = yAxisMaxAmplitude.minus(
                    yAxisMaxAmplitude.times(
                        normalizeValue(
                            value,
                            yAxisIntervals.max(),
                            yAxisIntervals.min()
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

        // TEst data
        val data = mutableMapOf(
            LocalDate.now().toEpochDay().toFloat() to 65.6f,
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
            LocalDate.now().plusDays(14).toEpochDay().toFloat() to 156.57f,
            LocalDate.now().plusDays(15).toEpochDay().toFloat() to 200.89f,
            LocalDate.now().plusDays(20).toEpochDay().toFloat() to 300.45f,
            LocalDate.now().plusDays(30).toEpochDay().toFloat() to 67.45f,
        )

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = data.toPersistentMap()
        )
    }
}