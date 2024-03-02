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

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem
import com.criticalay.neer.ui.composables.timepicker.TimeDialog
import com.criticalay.neer.utils.TimeUtils
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedTime(
    modifier: Modifier = Modifier,
    userBedTime:LocalTime,
    onTimeSelected: (time: LocalTime) -> Unit,
) {
    var showTimeDialog by remember {
        mutableStateOf(false)
    }
    val defaultSelectedTime by remember { mutableStateOf(userBedTime) }
    val timeState = rememberTimePickerState(
        initialHour = defaultSelectedTime.hour,
        initialMinute = defaultSelectedTime.minute
    )

    val formattedTime = remember(timeState.hour, timeState.minute) {
        TimeUtils.formatTime(timeState.hour, timeState.minute, timeState.hour < 12)
    }
    SettingItem(modifier = modifier .clickable {
        showTimeDialog = true
    }) {
        Row(
            modifier = Modifier
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                fontSize = 18.sp,
                text = stringResource(R.string.sleep_time)
            )

            Text(
                fontSize = 18.sp,
                text = formattedTime
            )
        }
    }

    TimeDialog(
        showDialog = showTimeDialog,
        onDismissRequest = { showTimeDialog = false },
        onConfirm = { selectedTime ->
            showTimeDialog = false
            onTimeSelected(selectedTime)
        },
        timeState = timeState
    )
}