/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
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

package com.criticalay.neer.ui.composables.waterdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.criticalay.neer.R
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.NotificationEvent
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.data.model.User
import com.criticalay.neer.hydration.HydrationPlan
import com.criticalay.neer.ui.composables.notification.dialog.NotificationPermissionSheet
import com.criticalay.neer.ui.composables.userdetails.DetailTextField
import com.criticalay.neer.utils.Constants.USER_ID
import com.criticalay.neer.utils.Converters
import com.criticalay.neer.utils.PreferencesManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

private enum class Step { ChooseGoal, EnableReminders }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterDetailForm(
    onProceed: () -> Unit,
    userDetails: User,
    neerEventListener: (neerEvent: NeerEvent) -> Unit,
) {
    val context = LocalContext.current
    val wakeTime = userDetails.wakeUpTime ?: LocalTime.of(7, 0)
    val sleepTime = userDetails.bedTime ?: LocalTime.of(23, 0)

    val recommendedMl = remember(userDetails) {
        if (userDetails.weight > 0.0)
            HydrationPlan.computeDailyGoalMl(
                weight = userDetails.weight,
                gender = userDetails.gender,
                ageYears = userDetails.age,
                units = userDetails.unit
            )
        else 0
    }

    var step by remember { mutableStateOf(Step.ChooseGoal) }
    var committedGoalMl by remember { mutableIntStateOf(0) }
    var showPermissionSheet by remember { mutableStateOf(false) }

    val commitGoal: (Int) -> Unit = { amount ->
        committedGoalMl = amount
        neerEventListener(
            NeerEvent.TriggerBeverageEvent(
                BeverageEvent.AddBeverage(
                    Beverage(
                        userId = USER_ID,
                        beverageName = context.getString(R.string.water),
                        totalIntakeAmount = amount
                    )
                )
            )
        )
        step = Step.EnableReminders
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.water_detail_header)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.water_detail_sub),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (step) {
                Step.ChooseGoal -> {
                    if (recommendedMl > 0) {
                        RecommendedGoalCard(
                            goalMl = recommendedMl,
                            unitLabel = Converters.getUnitName(userDetails.unit, 1),
                            hasAge = userDetails.age != null,
                            onUseRecommended = { commitGoal(recommendedMl) }
                        )
                    }
                    ManualGoalSection(
                        unitLabel = Converters.getUnitName(userDetails.unit, 1),
                        onSave = { commitGoal(it) }
                    )
                }

                Step.EnableReminders -> {
                    RemindersCard(
                        goalMl = committedGoalMl,
                        unitLabel = Converters.getUnitName(userDetails.unit, 1),
                        onEnable = { showPermissionSheet = true },
                        onSkip = {
                            PreferencesManager(context).saveNotificationPreference(false)
                            onProceed()
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showPermissionSheet) {
        NotificationPermissionSheet(
            onGranted = {
                val schedule = HydrationPlan.generateSchedule(
                    goalMl = committedGoalMl,
                    wakeTime = wakeTime,
                    sleepTime = sleepTime
                )
                val alarms = schedule.map { slot ->
                    slot.toAlarmItem(
                        title = context.getString(R.string.notification_title),
                        message = context.getString(
                            R.string.plan_reminder_body,
                            slot.amountMl,
                            Converters.getUnitName(userDetails.unit, 1)
                        )
                    )
                }
                neerEventListener(
                    NeerEvent.TriggerNotificationEvent(NotificationEvent.ReplaceAllAlarms(alarms))
                )
                PreferencesManager(context).saveNotificationPreference(true)
                showPermissionSheet = false
                onProceed()
            },
            onDismiss = { showPermissionSheet = false }
        )
    }
}

@Composable
private fun RecommendedGoalCard(
    goalMl: Int,
    unitLabel: String,
    hasAge: Boolean,
    onUseRecommended: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.water_detail_recommended_card),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$goalMl",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = unitLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            val ageSuffix = if (hasAge) " and age" else ""
            Text(
                text = stringResource(
                    R.string.water_detail_recommended_sub,
                    ageSuffix,
                    goalMl,
                    unitLabel
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onUseRecommended,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.water_detail_use_recommended))
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = stringResource(R.string.water_detail_learn_more),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun ManualGoalSection(
    unitLabel: String,
    onSave: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var typed by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.water_detail_manual_title),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(
                            if (expanded) R.string.water_detail_manual_collapse
                            else R.string.water_detail_manual_expand
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    DetailTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = typed,
                        onValueChange = { newValue ->
                            if (newValue.isDigitsOnly()) typed = newValue
                        },
                        label = stringResource(R.string.water_amount, unitLabel),
                        placeholder = stringResource(R.string.enter_your_water_intake, unitLabel),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            onSave(typed.toInt())
                        },
                        enabled = typed.isNotEmpty() && (typed.toIntOrNull() ?: 0) > 0,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(stringResource(R.string.water_detail_manual_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun RemindersCard(
    goalMl: Int,
    unitLabel: String,
    onEnable: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.water_detail_reminders_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.water_detail_reminders_card_sub),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$goalMl $unitLabel",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onEnable,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(R.string.water_detail_reminders_enable))
            }
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.water_detail_reminders_skip))
            }
        }
    }
}

private fun HydrationPlan.ScheduleSlot.toAlarmItem(
    title: String,
    message: String
): AlarmItem {
    val todayBase = LocalDate.now()
    val fireDate = if (time.isBefore(LocalTime.now())) todayBase.plusDays(1) else todayBase
    return AlarmItem(
        time = LocalDateTime.of(fireDate, time),
        interval = null,
        title = title,
        message = message,
        repeating = true,
        alarmState = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWaterDetailForm() {
    WaterDetailForm(
        onProceed = {},
        neerEventListener = {},
        userDetails = User(name = "ashish", weight = 72.0)
    )
}
