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

package com.criticalay.neer.ui.composables.userdetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.data.model.User
import com.criticalay.neer.hydration.HydrationPlan
import com.criticalay.neer.ui.composables.notification.dialog.NotificationPermissionSheet
import com.criticalay.neer.ui.composables.userdetails.time.SleepTimePicker
import com.criticalay.neer.ui.composables.userdetails.time.WakeUpTimePicker
import com.criticalay.neer.utils.Constants.USER_ID
import com.criticalay.neer.utils.Converters.getUnitName
import com.criticalay.neer.utils.PreferencesManager
import com.criticalay.neer.utils.SleepCycle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailForm(
    onProceed: () -> Unit,
    neerEventListener: (neerEvent: NeerEvent) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var userName by remember { mutableStateOf("") }
    var userAge by remember { mutableStateOf("") }
    var userHeight by remember { mutableStateOf("") }
    var userWeight by remember { mutableStateOf("") }
    var userGender by remember { mutableStateOf(Gender.FEMALE) }
    var userSelectedUnit by remember { mutableStateOf(Units.KG_ML) }
    var selectedSleepTime by remember { mutableStateOf(LocalTime.of(23, 0)) }
    var selectedWakeTime by remember { mutableStateOf(LocalTime.of(7, 0)) }

    var useManualGoal by remember { mutableStateOf(false) }
    var manualGoal by remember { mutableStateOf("") }
    var remindersEnabled by remember { mutableStateOf(true) }

    var showPermissionSheet by remember { mutableStateOf(false) }
    var proceedFired by remember { mutableStateOf(false) }

    val nameFocus = remember { FocusRequester() }
    val ageFocus = remember { FocusRequester() }
    val heightFocus = remember { FocusRequester() }
    val weightFocus = remember { FocusRequester() }
    val manualGoalFocus = remember { FocusRequester() }

    val weightUnit = getUnitName(userSelectedUnit, 0)
    val intakeUnit = getUnitName(userSelectedUnit, 1)

    val weightDouble = userWeight.toDoubleOrNull() ?: 0.0
    val ageInt = userAge.toIntOrNull()

    val recommendedMl = remember(weightDouble, userGender, ageInt, userSelectedUnit) {
        if (weightDouble > 0.0)
            HydrationPlan.computeDailyGoalMl(
                weight = weightDouble,
                gender = userGender,
                ageYears = ageInt,
                units = userSelectedUnit
            )
        else 0
    }
    val manualGoalMl = manualGoal.toIntOrNull() ?: 0
    val effectiveGoalMl = if (useManualGoal) manualGoalMl else recommendedMl

    val submitEnabled = userName.isNotBlank() &&
        (ageInt ?: 0) in 1..120 &&
        (userHeight.toDoubleOrNull() ?: 0.0) > 0.0 &&
        weightDouble > 0.0 &&
        effectiveGoalMl >= 500

    val plannedSchedule = remember(effectiveGoalMl, selectedWakeTime, selectedSleepTime) {
        if (effectiveGoalMl > 0)
            HydrationPlan.generateSchedule(
                goalMl = effectiveGoalMl,
                wakeTime = selectedWakeTime,
                sleepTime = selectedSleepTime
            )
        else emptyList()
    }

    val commitAndProceed: (Boolean) -> Unit = commit@{ remindersGranted ->
        if (proceedFired) return@commit
        proceedFired = true
        keyboardController?.hide()
        val prefs = PreferencesManager(context)
        prefs.saveSleepCycleTime(SleepCycle.SLEEP_TIME, selectedSleepTime)
        prefs.saveSleepCycleTime(SleepCycle.WAKE_TIME, selectedWakeTime)
        prefs.saveNotificationPreference(remindersEnabled && remindersGranted)

        neerEventListener(
            NeerEvent.AddUser(
                User(
                    name = userName.trim(),
                    age = ageInt,
                    gender = userGender,
                    weight = weightDouble,
                    height = userHeight.toDouble(),
                    bedTime = selectedSleepTime,
                    wakeUpTime = selectedWakeTime,
                    unit = userSelectedUnit
                )
            )
        )
        neerEventListener(
            NeerEvent.TriggerBeverageEvent(
                BeverageEvent.AddBeverage(
                    Beverage(
                        userId = USER_ID,
                        beverageName = context.getString(R.string.water),
                        totalIntakeAmount = effectiveGoalMl
                    )
                )
            )
        )
        if (remindersEnabled && remindersGranted && plannedSchedule.isNotEmpty()) {
            val alarms = plannedSchedule.map { slot ->
                slot.toAlarmItem(
                    title = context.getString(R.string.notification_title),
                    message = context.getString(
                        R.string.plan_reminder_body,
                        slot.amountMl,
                        intakeUnit
                    )
                )
            }
            neerEventListener(
                NeerEvent.TriggerNotificationEvent(
                    NotificationEvent.ReplaceAllAlarms(alarms)
                )
            )
        }
        onProceed()
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(text = stringResource(R.string.enter_details)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                LinearProgressIndicator(
                    progress = { if (submitEnabled) 1f else 0.55f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    Button(
                        onClick = {
                            if (!submitEnabled) return@Button
                            if (remindersEnabled) {
                                showPermissionSheet = true
                            } else {
                                commitAndProceed(false)
                            }
                        },
                        enabled = submitEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = stringResource(
                                if (remindersEnabled) R.string.details_continue_with_plan
                                else R.string.details_continue
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
        Column(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IntroBlock()

            SectionCard(title = stringResource(R.string.details_section_name)) {
                DetailTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(nameFocus),
                    value = userName,
                    onValueChange = { userName = it },
                    label = stringResource(R.string.details_label_name),
                    placeholder = stringResource(R.string.details_hint_name),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { ageFocus.requestFocus() }
                    )
                )
            }

            SectionCard(title = stringResource(R.string.details_section_body)) {
                UnitPicker(
                    selected = userSelectedUnit,
                    onSelect = { userSelectedUnit = it }
                )
                Spacer(Modifier.height(14.dp))
                DetailTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(ageFocus),
                    value = userAge,
                    onValueChange = { v ->
                        if (v.isEmpty()) userAge = ""
                        else if (v.length <= 3 && v.isDigitsOnly()) userAge = v
                    },
                    label = stringResource(R.string.details_label_age),
                    placeholder = stringResource(R.string.details_placeholder_age),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Cake,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { heightFocus.requestFocus() }
                    )
                )
                Spacer(Modifier.height(10.dp))
                DetailTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(heightFocus),
                    value = userHeight,
                    onValueChange = { v ->
                        if (v.isEmpty()) userHeight = ""
                        else if (v.length <= 3 && v.isDigitsOnly()) userHeight = v
                    },
                    label = "${stringResource(R.string.details_label_height)} (${stringResource(R.string.details_unit_cm)})",
                    placeholder = stringResource(R.string.details_placeholder_height),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Height,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { weightFocus.requestFocus() }
                    )
                )
                Spacer(Modifier.height(10.dp))
                DetailTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(weightFocus),
                    value = userWeight,
                    onValueChange = { v ->
                        if (v.isEmpty()) userWeight = ""
                        else if (v.length <= 5 && v.isDigitsOnly()) userWeight = v
                    },
                    label = "${stringResource(R.string.details_label_weight)} ($weightUnit)",
                    placeholder = stringResource(R.string.details_placeholder_weight),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.MonitorWeight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                )
                Spacer(Modifier.height(14.dp))
                GenderPicker(
                    selected = userGender,
                    onSelect = { userGender = it }
                )
            }

            SectionCard(
                title = stringResource(R.string.details_section_routine),
                subtitle = stringResource(R.string.details_section_routine_sub)
            ) {
                WakeUpTimePicker(onTimeSelected = { selectedWakeTime = it })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                SleepTimePicker(onTimeSelected = { selectedSleepTime = it })
            }

            SectionCard(
                title = stringResource(R.string.details_section_goal),
                subtitle = stringResource(R.string.details_section_goal_sub)
            ) {
                if (recommendedMl > 0) {
                    GoalPreview(
                        goalMl = if (useManualGoal) manualGoalMl else recommendedMl,
                        unit = intakeUnit,
                        isManual = useManualGoal
                    )
                } else {
                    Text(
                        text = stringResource(R.string.details_goal_profile_needed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.details_goal_manual_toggle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useManualGoal,
                        onCheckedChange = { useManualGoal = it }
                    )
                }
                AnimatedVisibility(
                    visible = useManualGoal,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        DetailTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(manualGoalFocus),
                            value = manualGoal,
                            onValueChange = { v ->
                                if (v.isEmpty() || (v.length <= 5 && v.isDigitsOnly()))
                                    manualGoal = v
                            },
                            label = stringResource(R.string.details_goal_manual_label, intakeUnit),
                            placeholder = stringResource(R.string.details_goal_manual_placeholder),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            )
                        )
                    }
                }
            }

            SectionCard(title = stringResource(R.string.details_section_reminders)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.size(12.dp))
                    Text(
                        text = stringResource(R.string.details_reminders_toggle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = remindersEnabled,
                        onCheckedChange = { remindersEnabled = it }
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (remindersEnabled && plannedSchedule.isNotEmpty())
                        stringResource(R.string.details_reminders_sub, plannedSchedule.size)
                    else if (remindersEnabled)
                        stringResource(R.string.water_detail_reminders_card_sub)
                    else
                        stringResource(R.string.details_reminders_sub_off),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        }
    }

    if (showPermissionSheet) {
        NotificationPermissionSheet(
            onGranted = {
                showPermissionSheet = false
                commitAndProceed(true)
            },
            onDismiss = {
                if (showPermissionSheet) {
                    showPermissionSheet = false
                    commitAndProceed(false)
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        nameFocus.requestFocus()
    }
}

@Composable
private fun IntroBlock() {
    Column {
        Text(
            text = stringResource(R.string.details_section_intro),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.details_section_intro_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GoalPreview(goalMl: Int, unit: String, isManual: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.details_goal_recommended,
                        goalMl,
                        unit
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isManual) "Custom" else "From your body profile",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(Modifier.size(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
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
fun PreviewUserDetailForm() {
    UserDetailForm(onProceed = {}, neerEventListener = {})
}
