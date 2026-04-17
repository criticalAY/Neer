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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.ui.composables.home.hero.HydrationHero
import com.criticalay.neer.ui.composables.home.hero.QuickAddSheet
import com.criticalay.neer.ui.composables.home.water.RecordList
import com.criticalay.neer.ui.navigation.Destination
import com.criticalay.neer.ui.navigation.NeerBottomNavigationBar
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.utils.Constants.USER_ID
import com.criticalay.neer.utils.PreferencesManager
import com.criticalay.neer.utils.SleepCycle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    neerEventListener: (neerEvent: NeerEvent) -> Unit,
    todayIntake: Int,
    targetIntake: Int,
    userDetails: User,
    intakeList: List<Intake>,
    navigateToNotifications: () -> Unit,
    onTabSelect: (Destination) -> Unit = {}
) {
    val context = LocalContext.current
    val isPortrait =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    var showQuickAdd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val startOfNextDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
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
        userDetails.bedTime?.let { time ->
            PreferencesManager(context).saveSleepCycleTime(SleepCycle.SLEEP_TIME, time)
        }
        userDetails.wakeUpTime?.let { time ->
            PreferencesManager(context).saveSleepCycleTime(SleepCycle.WAKE_TIME, time)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = navigateToNotifications) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = stringResource(R.string.notification)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NeerBottomNavigationBar(
                currentRoute = Destination.HomeScreen.path,
                onTabSelect = onTabSelect
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuickAdd = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_water)) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(28.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            GreetingBlock(userName = userDetails.name, percent = percentOfGoal(todayIntake, targetIntake))
            Spacer(Modifier.height(12.dp))

            if (isPortrait) {
                HydrationHero(
                    todayIntake = todayIntake,
                    targetIntake = targetIntake,
                    selectedUnits = userDetails.unit
                )
            } else {
                Text(
                    text = "$todayIntake / $targetIntake",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(24.dp))

            TodayRecordHeader(intakeCount = intakeList.size)

            Spacer(Modifier.height(12.dp))

            RecordList(
                todayAllIntakes = intakeList,
                selectedUnits = userDetails.unit,
                neerEventListener = neerEventListener
            )

            Spacer(Modifier.height(96.dp))
        }
    }

    if (showQuickAdd) {
        val prefs = remember { PreferencesManager(context) }
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onConfirm = { amount ->
                prefs.setWaterAmount(amount)
                neerEventListener(
                    NeerEvent.TriggerIntakeEvent(
                        IntakeEvent.AddIntake(
                            Intake(
                                USER_ID,
                                BEVERAGE_ID,
                                amount,
                                LocalDateTime.now()
                            )
                        )
                    )
                )
            },
            initialAmount = PreferencesManager(context).getWaterAmount(),
            selectedUnits = userDetails.unit
        )
    }
}

@Composable
private fun GreetingBlock(userName: String?, percent: Int) {
    val hour = LocalTime.now().hour
    val greetingRes = when (hour) {
        in 5..11 -> R.string.good_morning
        in 12..16 -> R.string.good_afternoon
        in 17..21 -> R.string.good_evening
        else -> R.string.good_night
    }
    val subtitleRes = when {
        percent >= 100 -> R.string.home_subtitle_goal_reached
        percent >= 50 -> R.string.home_subtitle_on_track
        else -> R.string.home_subtitle_behind
    }
    val greeting = stringResource(greetingRes)
    val line = if (!userName.isNullOrBlank()) "$greeting, $userName" else greeting

    Column {
        Text(
            text = line,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(subtitleRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TodayRecordHeader(intakeCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.WaterDrop,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.today_record),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (intakeCount > 0) {
                val plural = if (intakeCount == 1) "" else "s"
                Text(
                    text = stringResource(R.string.today_record_subtitle, intakeCount, plural),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun percentOfGoal(todayIntake: Int, targetIntake: Int): Int {
    if (targetIntake <= 0) return 0
    return ((todayIntake.toFloat() / targetIntake.toFloat()) * 100f).toInt().coerceAtMost(999)
}

@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    Home(
        neerEventListener = {},
        todayIntake = 1200,
        targetIntake = 3000,
        intakeList = emptyList(),
        userDetails = User(),
        navigateToNotifications = {}
    )
}
