/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
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

package com.criticalay.neer.ui.composables.home.hero

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.sin

/**
 * An animated liquid-fill that sits behind the progress ring. Fill height is
 * driven by [progress] (0f..1f); two sinusoidal waves translate horizontally
 * to create a subtle "water surface" motion.
 */
@Composable
fun WaveGlass(
    modifier: Modifier = Modifier,
    progress: Float,
    primaryColor: Color,
    secondaryColor: Color,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 900),
        label = "waveFillProgress",
    )

    val transition = rememberInfiniteTransition(label = "waveSurface")
    val wavePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wavePhase",
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = minOf(size.width, size.height)
            val radius = diameter / 2f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            val circlePath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        left = centerX - radius,
                        top = centerY - radius,
                        right = centerX + radius,
                        bottom = centerY + radius,
                    ),
                )
            }

            clipPath(circlePath) {
                val surfaceY = centerY + radius - (2f * radius * animatedProgress)
                val amplitude = (radius * 0.055f).coerceAtLeast(4f)

                drawWave(
                    centerX = centerX,
                    radius = radius,
                    surfaceY = surfaceY,
                    amplitude = amplitude,
                    phase = wavePhase,
                    color = secondaryColor.copy(alpha = 0.35f),
                )
                drawWave(
                    centerX = centerX,
                    radius = radius,
                    surfaceY = surfaceY + amplitude * 0.4f,
                    amplitude = amplitude * 0.75f,
                    phase = wavePhase + 1.3f,
                    color = primaryColor.copy(alpha = 0.55f),
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawWave(
    centerX: Float,
    radius: Float,
    surfaceY: Float,
    amplitude: Float,
    phase: Float,
    color: Color,
) {
    val left = centerX - radius
    val right = centerX + radius
    val bottom = centerY + radius
    val wavelength = radius * 1.6f

    val path = Path().apply {
        moveTo(left, bottom)
        lineTo(left, surfaceY)
        var x = left
        while (x <= right) {
            val theta = (x - left) / wavelength * 2f * Math.PI.toFloat()
            val y = surfaceY + amplitude * sin(theta + phase)
            lineTo(x, y)
            x += 6f
        }
        lineTo(right, bottom)
        close()
    }
    drawPath(path = path, color = color)
}

private val androidx.compose.ui.graphics.drawscope.DrawScope.centerY: Float
    get() = size.height / 2f
