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
import com.criticalay.neer.utils.TimeUtils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WakeUpTimePicker(
    onTimeSelected: (time:LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var timeSelected by remember { mutableStateOf(false) }

    val defaultSelectedTime by remember { mutableStateOf(LocalTime.of(7, 0)) }
    val timeState = rememberTimePickerState(
        initialHour = defaultSelectedTime.hour,
        initialMinute = defaultSelectedTime.minute
    )
    val formattedTime = remember(timeState.hour, timeState.minute) {
        TimeUtils.formatTime(timeState.hour, timeState.minute, timeState.hour < 12)
    }
    if (!showDialog && !timeSelected) {
        onTimeSelected(defaultSelectedTime)
    }

    TimeDialog(
        showDialog = showDialog,
        onDismissRequest = { showDialog = false },
        onConfirm = { selectedTime ->
            showDialog = false
            timeSelected=true
            onTimeSelected(selectedTime)
        },
        timeState = timeState
    )

    WakeUpTimeItem(
        title = stringResource(id = R.string.wake_up_time),
        setTime = formattedTime,
        onSettingClicked = {
            showDialog = true
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ShowDialog(){
    WakeUpTimePicker(onTimeSelected = {
        // Do something with the selected time
    })
}