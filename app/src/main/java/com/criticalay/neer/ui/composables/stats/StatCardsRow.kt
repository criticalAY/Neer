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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.utils.Converters
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatCardsRow(
    intakeHistory: List<Intake>,
    targetIntake: Int,
    selectedUnits: Units
) {
    val today = LocalDate.now()
    val totalsByDate: Map<LocalDate, Int> = intakeHistory
        .groupBy { it.intakeDateTime.toLocalDate() }
        .mapValues { entry -> entry.value.sumOf { it.intakeAmount } }

    val currentStreak = computeCurrentStreak(totalsByDate, targetIntake, today)
    val bestDay = totalsByDate.maxByOrNull { it.value }
    val daysTracked = totalsByDate.size.coerceAtLeast(1)
    val averagePerDay = totalsByDate.values.sum() / daysTracked

    val unitLabel = Converters.getUnitName(selectedUnits, 1)
    val plural = if (currentStreak == 1) "" else "s"
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_streak_title),
            value = "$currentStreak",
            subtitle = stringResource(R.string.stats_current_streak, currentStreak, plural)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_average_title),
            value = "$averagePerDay",
            subtitle = unitLabel
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_best_day_title),
            value = bestDay?.value?.toString() ?: "—",
            subtitle = bestDay?.key?.format(dateFormatter) ?: unitLabel
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

private fun computeCurrentStreak(
    totalsByDate: Map<LocalDate, Int>,
    target: Int,
    today: LocalDate
): Int {
    if (target <= 0) return 0
    var streak = 0
    var cursor = today
    while (true) {
        val total = totalsByDate[cursor] ?: 0
        if (total < target) break
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}
