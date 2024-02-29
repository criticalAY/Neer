/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
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
                   // TODO: update the value

                },
                currentValue = intakeAmount,
            )
        }
    }
}