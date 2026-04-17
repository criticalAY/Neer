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

package com.criticalay.neer.ui.composables.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Intake
import java.time.LocalDate

@Composable
fun StreakHeatmap(
    intakeHistory: List<Intake>,
    targetIntake: Int
) {
    val today = LocalDate.now()
    val totalsByDate: Map<LocalDate, Int> = intakeHistory
        .groupBy { it.intakeDateTime.toLocalDate() }
        .mapValues { entry -> entry.value.sumOf { it.intakeAmount } }

    val days = (29 downTo 0).map { offset ->
        val date = today.minusDays(offset.toLong())
        date to (totalsByDate[date] ?: 0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.stats_heatmap_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))

            val rows = days.chunked(10)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    row.forEach { (_, amount) ->
                        HeatCell(
                            amount = amount,
                            target = targetIntake,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Pad trailing cells if row is short
                    repeat(10 - row.size) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun HeatCell(amount: Int, target: Int, modifier: Modifier = Modifier) {
    val intensity = when {
        target <= 0 || amount == 0 -> 0f
        amount >= target -> 1f
        else -> (amount.toFloat() / target.toFloat()).coerceIn(0.15f, 1f)
    }
    val primary = MaterialTheme.colorScheme.primary
    val base = MaterialTheme.colorScheme.secondaryContainer
    val color = if (intensity == 0f) base else lerpColor(base, primary, intensity)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
    )
}

private fun lerpColor(start: Color, stop: Color, fraction: Float): Color = Color(
    red = start.red + (stop.red - start.red) * fraction,
    green = start.green + (stop.green - start.green) * fraction,
    blue = start.blue + (stop.blue - start.blue) * fraction,
    alpha = start.alpha + (stop.alpha - start.alpha) * fraction
)
