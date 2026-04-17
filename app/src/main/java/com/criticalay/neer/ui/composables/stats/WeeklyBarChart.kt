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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.utils.Converters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyBarChart(
    intakeHistory: List<Intake>,
    targetIntake: Int,
    selectedUnits: Units
) {
    val today = LocalDate.now()
    val daily = remember(intakeHistory) {
        (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            val total = intakeHistory
                .filter { it.intakeDateTime.toLocalDate() == date }
                .sumOf { it.intakeAmount }
            date to total
        }
    }

    val chartMax = maxOf(targetIntake, daily.maxOf { it.second }, 1)
    val dowFormatter = DateTimeFormatter.ofPattern("EEE")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.stats_weekly_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    R.string.stats_average_value,
                    daily.map { it.second }.average().toInt(),
                    Converters.getUnitName(selectedUnits, 1)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                daily.forEach { (date, total) ->
                    val fraction = (total.toFloat() / chartMax.toFloat()).coerceIn(0f, 1f)
                    val hitGoal = total >= targetIntake && targetIntake > 0
                    val isToday = date == today
                    BarColumn(
                        fraction = fraction,
                        label = date.format(dowFormatter),
                        emphasize = isToday,
                        hitGoal = hitGoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BarColumn(
    fraction: Float,
    label: String,
    emphasize: Boolean,
    hitGoal: Boolean,
    modifier: Modifier = Modifier
) {
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 700),
        label = "bar-$label"
    )
    val barColor = when {
        hitGoal -> MaterialTheme.colorScheme.primary
        emphasize -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    }

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .fillMaxHeight(animated.coerceAtLeast(0.015f))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(barColor)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            style = if (emphasize)
                MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.primary)
            else
                MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}
