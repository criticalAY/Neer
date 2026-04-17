/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.criticalay.neer.ui.composables.progressbar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Animated circular progress ring. Draws a soft radial gradient wash, a track
 * stroke, and an animated sweep proportional to [initialValue] / [maxValue].
 * Text content is deliberately not drawn here — overlay composables on top.
 */
@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    initialValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    minValue: Int = 0,
    maxValue: Int = 100,
) {
    val target = if (maxValue <= minValue) 0f
    else ((initialValue - minValue).toFloat() / (maxValue - minValue)).coerceIn(0f, 1f)

    val animatedFraction by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "progressSweep"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 20f
            val circleRadius = minOf(height, width) / 2f
            val center = Offset(x = width / 2f, y = height / 2f)

            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.18f),
                        secondaryColor.copy(alpha = 0.06f)
                    )
                ),
                radius = circleRadius,
                center = center
            )

            drawCircle(
                style = Stroke(width = circleThickness),
                color = secondaryColor.copy(alpha = 0.45f),
                radius = circleRadius,
                center = center
            )

            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedFraction,
                style = Stroke(width = circleThickness, cap = StrokeCap.Round),
                useCenter = false,
                size = Size(width = circleRadius * 2f, height = circleRadius * 2f),
                topLeft = Offset(
                    (width - circleRadius * 2f) / 2f,
                    (height - circleRadius * 2f) / 2f
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomCircularProgressIndicator() {
    CustomCircularProgressIndicator(
        modifier = Modifier.size(250.dp),
        initialValue = 50,
        maxValue = 200,
        primaryColor = Color(0xFF00BFFF),
        secondaryColor = Color(0xFFA7C7E7)
    )
}
