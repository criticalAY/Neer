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

package com.criticalay.neer.ui.composables.userdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.criticalay.neer.R
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.data.model.User
import com.criticalay.neer.ui.composables.SectionSpacer
import com.criticalay.neer.ui.composables.userdetails.time.SleepTimePicker
import com.criticalay.neer.ui.composables.userdetails.time.WakeUpTimePicker
import com.criticalay.neer.utils.Converters.getUnitName
import com.criticalay.neer.utils.PreferencesManager
import com.criticalay.neer.utils.SleepCycle
import timber.log.Timber
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailForm(
    onProceed: () -> Unit,
    neerEventListener: (neerEvent: NeerEvent) -> Unit
) {
    var selectedSleepTime by remember {
        mutableStateOf(LocalTime.of(23, 0))
    }
    var selectedWakeTime by remember {
        mutableStateOf(LocalTime.of(7, 0))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
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
                mutableStateOf(Gender.FEMALE)
            }

            var userSelectedUnit by remember {
                mutableStateOf(Units.KG_ML)
            }
            val context = LocalContext.current

            val weightUnit = getUnitName(userSelectedUnit, 0)

            Column(modifier = Modifier.padding(8.dp)) {
                DetailTextField(
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


                DetailTextField(
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

                DetailTextField(
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

                DetailTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userWeight,
                    onValueChange = { newValue ->
                        if (newValue.length <= 5 && newValue.isDigitsOnly() && newValue.isNotBlank()) {
                            userWeight = newValue
                        } else if (newValue.isEmpty()) {
                            userWeight = "" // Clear the field if the input is empty
                        }
                    },
                    label = "Weight ($weightUnit)",
                    placeholder = "Enter your weight in $weightUnit",
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
                    selectedGender = userGender.genderValue,
                    onOptionSelected = { gender ->
                        userGender = gender
                        Timber.d("User gender", gender)
                    }
                )

                HorizontalDivider()

                WakeUpTimePicker(onTimeSelected = { wakeTime ->
                    selectedWakeTime = wakeTime
                })

                HorizontalDivider()

                SleepTimePicker(onTimeSelected = { sleepTime ->
                    selectedSleepTime = sleepTime
                })

                HorizontalDivider()

                UnitItem(
                    title = stringResource(R.string.units),
                    selectedUnit = userSelectedUnit,
                    onOptionSelected = { units ->
                        userSelectedUnit = units
                        Timber.d("User selected", units.name)

                    }
                )

                HorizontalDivider()
            }

            Spacer(modifier = Modifier.weight(1f))
            var submitButtonEnabled by remember {
                mutableStateOf(false)
            }
            submitButtonEnabled = userName.isNotEmpty() && userAge.isNotEmpty() && userHeight.isNotEmpty() && userWeight.isNotEmpty()

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = submitButtonEnabled,
                onClick = {
                        PreferencesManager(context).saveSleepCycleTime(SleepCycle.SLEEP_TIME, selectedSleepTime)
                        PreferencesManager(context).saveSleepCycleTime(SleepCycle.WAKE_TIME, selectedWakeTime)
                    neerEventListener(
                        NeerEvent.AddUser(
                            User(
                                userName,
                                userAge.toInt(),
                                userGender,
                                userWeight.toDouble(),
                                userHeight.toDouble(),
                                bedTime = selectedSleepTime,
                                wakeUpTime = selectedWakeTime
                            )
                        )
                    )
                    onProceed()
                }
            ) {
                Text(text = stringResource(R.string.proceed))

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailForm() {
    UserDetailForm(viewModel()) {}
}