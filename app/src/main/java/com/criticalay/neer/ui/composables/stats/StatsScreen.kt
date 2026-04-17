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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.criticalay.neer.ui.adaptive.isExpandedWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.ui.navigation.Destination
import com.criticalay.neer.ui.navigation.NeerBottomNavigationBar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    intakeHistory: List<Intake>,
    targetIntake: Int,
    selectedUnits: Units,
    neerEventListener: (NeerEvent) -> Unit,
    onTabSelect: (Destination) -> Unit
) {
    LaunchedEffect(Unit) {
        val start = LocalDateTime.of(LocalDate.now().minusDays(29), LocalTime.MIN)
        val end = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
        neerEventListener(
            NeerEvent.TriggerIntakeEvent(IntakeEvent.GetIntakeHistory(startDate = start, endDate = end))
        )
        neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.GetUserDetails))
        neerEventListener(NeerEvent.TriggerBeverageEvent(BeverageEvent.GetTargetAmount))
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.stats_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NeerBottomNavigationBar(
                currentRoute = Destination.Stats.path,
                onTabSelect = onTabSelect
            )
        }
    ) { padding ->
        val wide = isExpandedWidth()
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (intakeHistory.isEmpty()) {
                FirstSipHint()
            }
            WeeklyBarChart(
                intakeHistory = intakeHistory,
                targetIntake = targetIntake,
                selectedUnits = selectedUnits
            )
            if (wide) {
                // On landscape phone / tablet, keep the chart full-width above
                // but place the stat cards and the heatmap side-by-side so the
                // screen doesn't feel scroll-heavy.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCardsRow(
                            intakeHistory = intakeHistory,
                            targetIntake = targetIntake,
                            selectedUnits = selectedUnits
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        StreakHeatmap(
                            intakeHistory = intakeHistory,
                            targetIntake = targetIntake
                        )
                    }
                }
            } else {
                StatCardsRow(
                    intakeHistory = intakeHistory,
                    targetIntake = targetIntake,
                    selectedUnits = selectedUnits
                )
                StreakHeatmap(
                    intakeHistory = intakeHistory,
                    targetIntake = targetIntake
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FirstSipHint() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = com.criticalay.neer.R.drawable.stats_hero),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.stats_preview_headline),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    text = stringResource(R.string.stats_preview_sub),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
