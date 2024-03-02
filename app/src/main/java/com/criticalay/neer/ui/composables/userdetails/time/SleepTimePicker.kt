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

package com.criticalay.neer.ui.composables.userdetails.time

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.timepicker.TimeDialog
import com.criticalay.neer.utils.TimeUtils.formatTime
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimePicker(
    onTimeSelected: (time: LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var timeSelected by remember { mutableStateOf(false) }

    val defaultSelectedTime by remember { mutableStateOf(LocalTime.of(23, 0)) }
    val timeState = rememberTimePickerState(
        initialHour = defaultSelectedTime.hour,
        initialMinute = defaultSelectedTime.minute
    )
    val formattedTime = remember(timeState.hour, timeState.minute) {
        formatTime(timeState.hour, timeState.minute, timeState.hour < 12)
    }
    if (!showDialog && !timeSelected) {
        onTimeSelected(defaultSelectedTime)
    }

    TimeDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirm = { selectedTime ->
            showDialog = false
            onTimeSelected(selectedTime)
        },
        timeState = timeState
    )

    SleepTimeItem(
        title = stringResource(id = R.string.sleep_time),
        setTime = formattedTime,
        onSettingClicked = {
            timeSelected=true
            showDialog = true
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ShowSleepDialog() {
    SleepTimePicker(onTimeSelected = {
    })
}