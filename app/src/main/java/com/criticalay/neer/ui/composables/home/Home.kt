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

package com.criticalay.neer.ui.composables.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.criticalay.neer.R
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.ui.composables.home.alertdialog.SelectWaterAmountDialog
import com.criticalay.neer.ui.composables.home.water.RecordList
import com.criticalay.neer.ui.composables.progressbar.CustomCircularProgressIndicator
import com.criticalay.neer.ui.theme.Light_blue
import com.criticalay.neer.ui.viewmodel.SharedViewModel
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.utils.Constants.USER_ID
import com.criticalay.neer.utils.PreferencesManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    sharedViewModel: SharedViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            val context = LocalContext.current
            val showDialog = remember { mutableStateOf(false) }

            Text(
                text = stringResource(R.string.drink_target),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .padding(10.dp)
            ) {
                LaunchedEffect(Unit) {
                    val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
                    val startOfNextDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
                    sharedViewModel.handleIntakeEvent(IntakeEvent.GetTodayTotalIntake(startDay = startOfDay, endDay = startOfNextDay))
                }
                val todayTotalIntakes  = sharedViewModel.todayTotalIntake.collectAsState().value
                CustomCircularProgressIndicator(
                    initialValue = todayTotalIntakes,
                    maxValue = 200,
                    primaryColor = Color.Blue,
                    secondaryColor = Light_blue,
                    circleRadius = 230f,
                    onPositionChange = {}
                )

                if (showDialog.value)
                    SelectWaterAmountDialog(setShowDialog = { show ->
                        showDialog.value = show
                    }, onDismissRequest = { selectedValue ->
                        PreferencesManager(context = context).setWaterAmount(selectedValue)
                    }, currentValue = PreferencesManager(context = context).getWaterAmount())

                OutlinedButton(
                    onClick = { showDialog.value = true },
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.CenterEnd),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Water,
                        contentDescription = stringResource(id = R.string.change_the_water_intake_amount)
                    )
                }
            }

            Row(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {

                Button(
                    onClick = {
                        sharedViewModel.handleIntakeEvent(
                            IntakeEvent.AddIntake(
                                Intake(
                                    USER_ID,
                                    BEVERAGE_ID,
                                    PreferencesManager(context = context).getWaterAmount(),
                                    LocalDateTime.now()
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
                sharedViewModel = sharedViewModel,
                intakeEventListener = sharedViewModel::handleIntakeEvent
            )

        }

    }
}


@Composable
@Preview(showBackground = true)
fun PreviewHome() {
    Home(sharedViewModel = viewModel())
}