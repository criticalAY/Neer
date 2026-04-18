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

package com.criticalay.neer.ui.composables.home.water

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.ui.composables.home.alertdialog.AmountEditDialog
import com.criticalay.neer.utils.Converters
import com.criticalay.neer.utils.TimeUtils.formatLocalDateTimeToTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import timber.log.Timber

@Composable
fun RecordList(
    modifier: Modifier = Modifier,
    todayAllIntakes: List<Intake>,
    selectedUnits: Units,
    neerEventListener: (neerEvent: NeerEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val startOfNextDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
        neerEventListener(
            NeerEvent.TriggerIntakeEvent(
                IntakeEvent.GetTodayIntake(
                    startDay = startOfDay,
                    endDay = startOfNextDay,
                ),
            ),
        )
    }

    var selectedIntakeId by remember { mutableLongStateOf(0L) }
    var intakeAmount by remember { mutableIntStateOf(0) }
    val showDialog = remember { mutableStateOf(false) }

    if (todayAllIntakes.isEmpty()) {
        EmptyState(modifier = modifier.fillMaxWidth())
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            todayAllIntakes.forEachIndexed { index, intake ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 3 },
                    exit = fadeOut(),
                ) {
                    val time = formatLocalDateTimeToTime(intake.intakeDateTime)
                    WaterRecordItem(
                        handleDelete = {
                            neerEventListener(
                                NeerEvent.TriggerIntakeEvent(IntakeEvent.DeleteIntake(intake)),
                            )
                        },
                        handleEdit = {
                            Timber.d("Edit water amount dialog shown")
                            selectedIntakeId = intake.intakeId
                            intakeAmount = intake.intakeAmount
                            showDialog.value = true
                        },
                        waterIntakeTime = time,
                        waterIntakeAmount = "${intake.intakeAmount} ${Converters.getUnitName(selectedUnits, 1)}",
                    )
                }
                if (index != todayAllIntakes.lastIndex) Spacer(Modifier.height(10.dp))
            }
        }

        if (showDialog.value) {
            AmountEditDialog(
                setShowDialog = { show -> showDialog.value = show },
                onDismissRequest = { newValue ->
                    Timber.d("Updated selected item water amount")
                    neerEventListener(
                        NeerEvent.TriggerIntakeEvent(
                            IntakeEvent.UpdateIntakeById(
                                intakeId = selectedIntakeId,
                                intakeAmount = newValue,
                            ),
                        ),
                    )
                },
                currentValue = intakeAmount,
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.empty_glass_hero),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_today_headline),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.empty_today_subcopy),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
