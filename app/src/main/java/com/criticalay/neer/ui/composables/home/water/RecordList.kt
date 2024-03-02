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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.ui.composables.home.alertdialog.AmountEditDialog
import com.criticalay.neer.ui.viewmodel.SharedViewModel
import com.criticalay.neer.utils.TimeUtils.formatLocalDateTimeToTime
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun RecordList(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel,
    intakeEventListener: (intakeEvent: IntakeEvent) -> Unit,
) {
    LaunchedEffect(Unit) {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val startOfNextDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
        intakeEventListener(
            IntakeEvent.GetTodayIntake(
                startDay = startOfDay,
                endDay = startOfNextDay
            )
        )
    }

    val lazyListState = rememberLazyListState()
    val todayAllIntakes = sharedViewModel.todayAllIntakes.collectAsState().value

    LaunchedEffect(todayAllIntakes) {
        lazyListState.animateScrollToItem(0)
    }
    var selectedIntakeId by remember { mutableLongStateOf(0L) }

    var intakeAmount by remember {
        mutableIntStateOf(0)
    }

    if (todayAllIntakes.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                modifier = Modifier
                    .sizeIn(50.dp, 50.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.ic_rounded_glass_cup),
                contentDescription = null
            )

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.no_water_consumed)
            )

        }
    } else {
        val showDialog = remember { mutableStateOf(false) }
        LazyColumn(state = lazyListState, modifier = modifier) {
            items(todayAllIntakes, key = { intakeId -> intakeId.intakeId }) { intake ->
                val time = formatLocalDateTimeToTime(intake.intakeDateTime)
                intakeAmount = intake.intakeAmount
                WaterRecordItem(
                    handleDelete = {
                        intakeEventListener(IntakeEvent.DeleteIntake(intake))
                    },
                    handleEdit = {
                        Timber.d("Edit water amount dialog shown")
                        selectedIntakeId = intake.intakeId
                        showDialog.value = true

                    },
                    waterIntakeTime = time,
                    waterIntakeAmount = "$intakeAmount ml"
                )
            }
        }

        if (showDialog.value) {
            AmountEditDialog(
                setShowDialog = { show ->
                    showDialog.value = show
                },
                onDismissRequest = { newValue ->
                    Timber.d("Updated selected item water amount")
                    intakeEventListener(IntakeEvent.UpdateIntakeById(intakeId = selectedIntakeId, intakeAmount = newValue))
                },
                currentValue = intakeAmount,
            )
        }
    }
}