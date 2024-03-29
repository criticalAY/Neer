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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Support
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem
import com.criticalay.neer.ui.composables.userdetails.DetailTextField

@Composable
fun Height(
    modifier: Modifier = Modifier,
    userHeight:Double,
    newHeight: (value:Double) -> Unit
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
            Icon(
                modifier = Modifier.padding(end = 5.dp),
                imageVector = Icons.Rounded.Height,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .weight(1f).padding(start = 5.dp),
                fontSize = 18.sp,
                text = stringResource(R.string.height)
            )

            Text(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                text = stringResource(R.string.height_value, userHeight)
            )
        }
    }
    if (showDialog) {
        EditHeightDialog(
            height = userHeight,
            newValue = { height ->
                newHeight(height)

            },
            showAlertDialog = { show -> showDialog = show }
        )
    }
}


@Composable
private fun EditHeightDialog(
    modifier: Modifier = Modifier,
    height: Double,
    newValue: (height: Double) -> Unit,
    showAlertDialog: (show: Boolean) -> Unit,
) {
    var userHeight by remember { mutableStateOf(height.toString()) }

    Dialog(onDismissRequest = {
        // Do nothing
    }) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = stringResource(R.string.change_your_height),
                    fontSize = 20.sp
                )
                DetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userHeight,
                    onValueChange = { newValue ->
                        if (newValue.isNotEmpty()) {
                            userHeight = newValue
                        } else if (newValue.isEmpty()) {
                            userHeight = ""
                        }
                    },
                    label = stringResource(R.string.height),
                    placeholder = stringResource(R.string.change_your_height),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (userHeight.isNotBlank()) {
                            IconButton(onClick = { userHeight = "" }) {
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
                            newValue(userHeight.toDouble())
                            showAlertDialog(false)
                        },
                        modifier = Modifier.padding(8.dp),
                        enabled = userHeight.isNotBlank()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}