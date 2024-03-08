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

package com.criticalay.neer.ui.composables.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.ui.composables.home.alertdialog.SelectWaterAmountDialog
import com.criticalay.neer.ui.composables.home.water.RecordList
import com.criticalay.neer.ui.composables.progressbar.CustomCircularProgressIndicator
import com.criticalay.neer.ui.theme.Light_blue
import com.criticalay.neer.ui.theme.Progress_Blue
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.utils.Constants.USER_ID
import com.criticalay.neer.utils.PreferencesManager
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    neerEventListener: (neerEvent: NeerEvent) -> Unit,
    todayIntake: Int,
    targetIntake: Int,
    userDetails : User,
    intakeList: List<Intake>,
    navigateToSettings: () -> Unit,
    navigateToNotifications: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navigateToNotifications() }) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = stringResource(
                                R.string.notification
                            )
                        )

                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            val context = LocalContext.current
            val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT


            HorizontalDivider()

            Text(
                text = stringResource(R.string.drink_target),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(8.dp)
            )

            if (isPortrait) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .padding(4.dp)
            ) {
                LaunchedEffect(Unit) {
                    val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                    val startOfNextDay =
                        LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
                    neerEventListener(
                        NeerEvent.TriggerIntakeEvent(
                            IntakeEvent.GetTodayTotalIntake(
                                startDay = startOfDay,
                                endDay = startOfNextDay
                            )
                        )
                    )
                    neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.GetUserDetails))
                    neerEventListener(NeerEvent.TriggerBeverageEvent(BeverageEvent.GetTargetAmount))
                }

                Timber.d("Intake total %s", todayIntake)
                    CustomCircularProgressIndicator(
                        modifier = Modifier.padding(25.dp),
                        initialValue = todayIntake,
                        maxValue = targetIntake,
                        primaryColor = Progress_Blue,
                        selectedUnits = userDetails.unit,
                        secondaryColor = Light_blue,
                        onPositionChange = {}
                    )


                    ChangeIntakeAmountDialog(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(10.dp)
                    )
                }
            }else{
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "$todayIntake/$targetIntake ml")
                }
            }

            Row(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {

                Button(
                    onClick = {
                        neerEventListener(
                            NeerEvent.TriggerIntakeEvent(
                                IntakeEvent.AddIntake(
                                    Intake(
                                        USER_ID,
                                        BEVERAGE_ID,
                                        PreferencesManager(context = context).getWaterAmount(),
                                        LocalDateTime.now()
                                    )
                                )
                            )
                        )
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_water_full),
                        contentDescription = stringResource(
                            R.string.add_water
                        )
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(
                            R.string.add_water
                        )
                    )

                }
            }

            Spacer(Modifier.size(26.dp))

            HorizontalDivider()

            Spacer(Modifier.size(26.dp))

            Row(modifier = Modifier.padding(start = 8.dp)) {
                Icon(imageVector = Icons.Filled.WaterDrop, contentDescription = null)

                Spacer(Modifier.size(8.dp))

                Text(
                    fontSize = 18.sp,
                    modifier = Modifier,
                    text = stringResource(R.string.today_record)
                )
            }

            RecordList(
                modifier = Modifier.padding(8.dp),
                todayAllIntakes = intakeList,
                selectedUnits = userDetails.unit,
                neerEventListener = neerEventListener
            )

        }

    }
}

@Composable
private fun ChangeIntakeAmountDialog(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showDialog.value = true },
        modifier = modifier
            .size(30.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Water,
            contentDescription = stringResource(id = R.string.change_the_water_intake_amount)
        )
    }

    if (showDialog.value)
        SelectWaterAmountDialog(setShowDialog = { show ->
            showDialog.value = show
        }, onDismissRequest = { selectedValue ->
            PreferencesManager(context = context).setWaterAmount(selectedValue)
        }, currentValue = PreferencesManager(context = context).getWaterAmount())
}

@Preview(showBackground = true)
@Composable
fun PreviewHome(){
    Home(
        neerEventListener = {},
        todayIntake = 500,
        targetIntake = 5000,
        intakeList = emptyList(),
        userDetails = User(),
        navigateToSettings = { /*TODO*/ }) {

    }
}
