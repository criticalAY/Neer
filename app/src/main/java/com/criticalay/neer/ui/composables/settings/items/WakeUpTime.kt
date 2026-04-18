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

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.timepicker.TimeDialog
import com.criticalay.neer.utils.TimeUtils.formatTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpTime(
    modifier: Modifier = Modifier,
    userWakeTime: LocalTime,
    onTimeSelected: (time: LocalTime) -> Unit,
) {
    var showTimeDialog by remember {
        mutableStateOf(false)
    }
    val defaultSelectedTime by remember { mutableStateOf(userWakeTime) }
    val timeState = rememberTimePickerState(
        initialHour = defaultSelectedTime.hour,
        initialMinute = defaultSelectedTime.minute,
    )

    val formattedTime = remember(timeState.hour, timeState.minute) {
        formatTime(timeState.hour, timeState.minute, timeState.hour < 12)
    }
    com.criticalay.neer.ui.composables.settings.SettingsRow(
        icon = R.drawable.ic_sun,
        title = stringResource(R.string.wake_up_time),
        trailingValue = formattedTime,
        onClick = { showTimeDialog = true },
        modifier = modifier,
    )

    TimeDialog(
        showDialog = showTimeDialog,
        onDismissRequest = { showTimeDialog = false },
        onConfirm = { selectedTime ->
            showTimeDialog = false
            onTimeSelected(selectedTime)
        },
        timeState = timeState,
    )
}
