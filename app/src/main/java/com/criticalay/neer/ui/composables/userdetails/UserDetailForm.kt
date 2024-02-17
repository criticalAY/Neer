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

package com.criticalay.neer.ui.composables.userdetails


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.ui.composables.SectionSpacer
import com.criticalay.neer.ui.composables.userdetails.time.SleepTimePicker
import com.criticalay.neer.ui.composables.userdetails.time.WakeUpTimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailForm() {

    var selectedSleepHour by remember { mutableIntStateOf(0) }
    var selectedSleepMinute by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = selectedSleepHour,
        initialMinute = selectedSleepMinute
    )

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.enter_details))
            })
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            HorizontalDivider()

            var userName by remember {
                mutableStateOf("")
            }
            var userAge by remember {
                mutableStateOf("")
            }
            var userHeight by remember {
                mutableStateOf("")
            }
            var userWeight by remember {
                mutableStateOf("")
            }

            var userGender by remember {
                mutableStateOf("")
            }

            var userSleepTime by remember {
                mutableStateOf("")
            }

            var userWakeUpTime by remember {
                mutableStateOf("")
            }

            var userSelectedUnit by remember {
                mutableStateOf("")
            }

            Column(modifier = Modifier.padding(8.dp)) {
                UserDetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userName,
                    onValueChange = { newValue -> userName = newValue },
                    label = "Name",
                    placeholder = "Enter your name",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    }
                )


                UserDetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userAge,
                    onValueChange = { newValue ->
                        if (newValue.length <= 3 && newValue.isDigitsOnly() && newValue.isNotBlank()) {
                            userAge = newValue
                        } else if (newValue.isEmpty()) {
                            userAge = ""
                        }
                    },
                    label = "Age",
                    placeholder = "Enter your age",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )

                UserDetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userHeight,
                    onValueChange = { newValue ->
                        if (newValue.length <= 3 && newValue.isDigitsOnly() && newValue.isNotBlank()) {
                            userHeight = newValue
                        } else if (newValue.isEmpty()) {
                            userHeight = ""
                        }
                    },
                    label = "Height(Cm)",
                    placeholder = "Enter your height in cm",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                )

                UserDetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userWeight,
                    onValueChange = { newValue ->
                        if (newValue.length <= 3 && newValue.isDigitsOnly() && newValue.isNotBlank()) {
                            userWeight = newValue
                        } else if (newValue.isEmpty()) {
                            userWeight = ""
                        }
                                    },
                    label = "Weight",
                    placeholder = "Enter your height",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }


            SectionSpacer(modifier = Modifier.fillMaxWidth())
            Column(modifier = Modifier.padding(8.dp)) {

                GenderItem(
                    title = stringResource(R.string.gender),
                    selectedGender = Gender.FEMALE
                ) {

                }

                HorizontalDivider()

                WakeUpTimePicker(onTimeSelected = { hour, minute ->
                    // Do something with the selected time
                })

                HorizontalDivider()

                SleepTimePicker(onTimeSelected = { hour, minute ->
                    // Do something with the selected time
                })

                HorizontalDivider()

                UnitItem(title = "Unit", setUnit = "kg/ml") {

                }

                HorizontalDivider()
            }

        }

        if (showDialog) {
            BasicAlertDialog(
                onDismissRequest = { showDialog = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(color = Color.LightGray.copy(alpha = .3f))
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timeState)
                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text(text = "Dismiss")
                        }
                        TextButton(onClick = {
                            showDialog = false
                            selectedSleepHour = timeState.hour
                            selectedSleepMinute = timeState.minute
                        }) {
                            Text(text = "Confirm")
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailForm() {
    UserDetailForm()
}