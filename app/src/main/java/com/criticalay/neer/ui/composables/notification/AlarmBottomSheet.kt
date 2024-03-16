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

package com.criticalay.neer.ui.composables.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBottomSheet(
    modifier: Modifier = Modifier,
    timeState: TimePickerState,
    title:String,
    onConfirm: (time: LocalTime) -> Unit,
    showBottomSheet: (state: Boolean) -> Unit,
    repeatable:(repeating:Boolean) -> Unit,
    deleteListener: (() -> Unit?)? = null,
    defaultChip: ChipType = ChipType.RemindOnce
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedChip by remember { mutableStateOf(defaultChip) }
    ModalBottomSheet(
        modifier = modifier,
        dragHandle = null,
        onDismissRequest = {
            showBottomSheet(false)
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp),
            ) {
                IconButton(
                    onClick = { showBottomSheet(false) }) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }

                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontWeight = FontWeight(500)),
                    fontSize = 20.sp,
                    text = title
                )

                IconButton(
                    onClick = {
                        val selectedTime = LocalTime.of(timeState.hour, timeState.minute)
                        onConfirm(selectedTime)
                        showBottomSheet(false)
                    }) {
                    Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                }

            }
            TimePicker(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                state = timeState
            )
            HorizontalDivider()

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                FilterChip(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        if (selectedChip != ChipType.RemindOnce) {
                            selectedChip =  ChipType.RemindOnce
                        }
                        repeatable(false)
                    },
                    label = { Text("Remind once") },
                    selected = selectedChip == ChipType.RemindOnce,
                    leadingIcon = if (selectedChip == ChipType.RemindOnce) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )

                FilterChip(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        if (selectedChip != ChipType.RemindDaily) {
                            selectedChip =ChipType.RemindDaily
                        }
                        repeatable(true)
                    },
                    label = { Text("Remind daily") },
                    selected = selectedChip == ChipType.RemindDaily,
                    leadingIcon = if (selectedChip == ChipType.RemindDaily) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null
                )
            }

            if(title== stringResource(R.string.edit_notification)){
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    onClick = { deleteListener?.invoke() }) {
                    Text(text = "Delete")
                }
            }
        }
    }
}

enum class ChipType {
    RemindOnce,
    RemindDaily
}