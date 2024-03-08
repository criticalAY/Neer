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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.ui.composables.settings.SettingItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.ui.composables.userdetails.DetailTextField
import com.criticalay.neer.utils.Converters

@Composable
fun Weight(
    modifier: Modifier = Modifier,
    userWeight:Double,
    selectedUnits: Units,
    newWeight: (weight:Double) -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    SettingItem(modifier = modifier .clickable {
        showDialog = true
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
                text = stringResource(R.string.weight)
            )

            Text(
                fontSize = 18.sp,
                text = "$userWeight ${Converters.getUnitName(selectedUnits, 0)}"
            )
        }
    }
    if (showDialog) {
        EditWeightDialog(
            weight = userWeight,
            newValue = { weight ->
                newWeight(weight)

            },
            showAlertDialog = { show -> showDialog = show }
        )
    }
}

@Composable
private fun EditWeightDialog(
    modifier: Modifier = Modifier,
    weight: Double,
    newValue: (weight: Double) -> Unit,
    showAlertDialog: (show: Boolean) -> Unit,
) {
    var userWeight by remember { mutableStateOf(weight.toString()) }

    Dialog(onDismissRequest = {
        // Do nothing
    }) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = stringResource(R.string.change_your_weight),
                    fontSize = 20.sp
                )
                DetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userWeight,
                    onValueChange = { newValue ->
                        if (newValue.isNotEmpty()) {
                            userWeight = newValue
                        } else if (newValue.isEmpty()) {
                            userWeight = ""
                        }
                    },
                    label = stringResource(R.string.weight),
                    placeholder = stringResource(R.string.change_your_weight),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (userWeight.isNotBlank()) {
                            IconButton(onClick = { userWeight = "" }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(R.string.clear)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showAlertDialog(false) }, modifier = Modifier.padding(8.dp)) {
                        Text(stringResource(R.string.dismiss))
                    }
                    TextButton(
                        onClick = {
                            newValue(userWeight.toDouble())
                            showAlertDialog(false)
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = userWeight.isNotBlank()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}
