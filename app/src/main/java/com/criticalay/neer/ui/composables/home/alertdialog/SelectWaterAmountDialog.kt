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

package com.criticalay.neer.ui.composables.home.alertdialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.criticalay.neer.R

@Composable
fun SelectWaterAmountDialog(
    modifier: Modifier = Modifier,
    setShowDialog: (Boolean) -> Unit,
    onDismissRequest: (Int) -> Unit,
    currentValue: Int,
) {
    val selectedMap = remember { mutableStateMapOf(currentValue to true) }

    val waterAmountItem = arrayListOf(100, 125, 150, 175, 200, 300, 400, 500)

    Dialog(
        onDismissRequest = {
            // Do nothing
        }) {
        Column(
            modifier = modifier.padding(10.dp)
        ) {
            Card(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp),
                    text = stringResource(R.string.change_the_water_intake_amount)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(waterAmountItem, key = { value ->
                        value.dec()

                    }) { amount ->
                        val isSelected = selectedMap.getOrDefault(amount, false)
                        FilterChip(
                            modifier = Modifier.wrapContentWidth(),
                            onClick = {
                                if (!isSelected) {
                                    selectedMap[amount] = !isSelected

                                    // Deselect all other chips
                                    selectedMap.keys.filter { it != amount }.forEach { key ->
                                        selectedMap[key] = false
                                    }
                                }
                            },
                            label = {
                                Text(
                                    maxLines = 1,
                                    text = "$amount ml"
                                )
                            },
                            selected = isSelected,
                            leadingIcon = {
                                Image(
                                    painter = painterResource(
                                        id = R.drawable.ic_juice_glass
                                    ), contentDescription = null
                                )
                            },
                            trailingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = stringResource(R.string.done),
                                    )
                                }
                            } else {
                                null
                            },
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    val selectedKey = selectedMap.keys.firstOrNull { selectedMap[it] == true }
                    if (selectedKey != null) {
                        onDismissRequest(selectedKey)
                    }
                    setShowDialog(false)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    .border(2.dp, MaterialTheme.colorScheme.outline, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(R.string.close)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewEditWaterAmountDialog() {
    SelectWaterAmountDialog(onDismissRequest = {

    }, setShowDialog = {

    }, currentValue = 100)
}