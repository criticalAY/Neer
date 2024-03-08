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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.res.stringResource

@Composable
fun WaterRecordItem(
    modifier: Modifier = Modifier,
    handleDelete: () -> Unit,
    handleEdit: () -> Unit,
    waterIntakeTime: String,
    waterIntakeAmount: String
) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedCard {
        Row(modifier = modifier
            .fillMaxWidth()
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .weight(0.2f)
                    .sizeIn(minWidth = 30.dp, minHeight = 30.dp),
                painter = painterResource(id = R.drawable.ic_outline_water_bottle),
                contentDescription = null
            )
            Spacer(Modifier.size(16.dp))
            Text(
                modifier = Modifier.weight(0.4f),
                fontSize = 20.sp,
                text = waterIntakeAmount
            )
            Spacer(Modifier.size(16.dp))
            Text(
                modifier = Modifier.weight(0.4f),
                fontSize = 20.sp,
                text = waterIntakeTime
            )

            // Wrap the IconButton and DropdownMenu in a Box
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd) // Position the DropdownMenu at the top end
                ) {
                    DropdownMenuItem(text = { Text(text = stringResource(R.string.edit)) },
                        onClick = {
                           handleEdit()
                            expanded = false
                        },
                        Modifier.offset(16.dp, 0.dp)
                    )
                    DropdownMenuItem(text = { Text(text = stringResource(R.string.delete)) },
                        onClick = {
                            handleDelete()
                            expanded = false
                        },
                        Modifier.offset(16.dp, 0.dp)
                    )

                }
            }
        }
    }
    Spacer(Modifier.size(10.dp))
}

@Preview(showBackground = true)
@Composable
fun PreviewWaterRecordItem(){
    WaterRecordItem(waterIntakeAmount = "300ml", waterIntakeTime = "12:53 AM", handleDelete = {}, handleEdit = {})
}