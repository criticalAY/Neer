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

package com.criticalay.neer.ui.composables.hydrationplan

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.NotificationEvent
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.data.model.User
import com.criticalay.neer.hydration.HydrationPlan
import com.criticalay.neer.utils.Converters
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HydrationPlanScreen(
    userDetails: User,
    neerEventListener: (NeerEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val profileComplete = userDetails.weight > 0.0 &&
        userDetails.wakeUpTime != null &&
        userDetails.bedTime != null
    var showInfo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hydration_plan_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showInfo = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.plan_info_description)
                        )
                    }
                },
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
            if (!profileComplete) {
                EmptyProfileNotice()
                return@Column
            }

            val breakdown = remember(userDetails) {
                HydrationPlan.explain(
                    weight = userDetails.weight,
                    gender = userDetails.gender,
                    ageYears = userDetails.age,
                    units = userDetails.unit
                )
            }
            val schedule = remember(breakdown.goalMl, userDetails.wakeUpTime, userDetails.bedTime) {
                HydrationPlan.generateSchedule(
                    goalMl = breakdown.goalMl,
                    wakeTime = userDetails.wakeUpTime!!,
                    sleepTime = userDetails.bedTime!!
                )
            }

            GoalCard(
                breakdown = breakdown,
                unitLabel = Converters.getUnitName(userDetails.unit, 1),
                gender = userDetails.gender,
                onApplyGoal = {
                    neerEventListener(
                        NeerEvent.TriggerBeverageEvent(BeverageEvent.UpdateTarget(breakdown.goalMl))
                    )
                    Toast.makeText(context, R.string.plan_goal_applied, Toast.LENGTH_SHORT).show()
                }
            )

            ScheduleCard(
                schedule = schedule,
                wakeTime = userDetails.wakeUpTime!!,
                sleepTime = userDetails.bedTime!!,
                unitLabel = Converters.getUnitName(userDetails.unit, 1),
                onEnableReminders = {
                    if (!ensureExactAlarmPermission(context)) return@ScheduleCard
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
                    Toast.makeText(
                        context,
                        R.string.plan_reminders_scheduled,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Text(
                text = stringResource(R.string.plan_research_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showInfo) {
        HydrationInfoSheet(
            userDetails = userDetails,
            onDismiss = { showInfo = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HydrationInfoSheet(
    userDetails: User,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val breakdown = remember(userDetails) {
        if (userDetails.weight > 0.0)
            HydrationPlan.explain(
                weight = userDetails.weight,
                gender = userDetails.gender,
                ageYears = userDetails.age,
                units = userDetails.unit
            )
        else null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.plan_info_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.plan_info_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            InfoStep(
                title = stringResource(R.string.plan_info_step1_title),
                body = step1Body(breakdown, userDetails.unit)
            )
            InfoStep(
                title = stringResource(R.string.plan_info_step2_title),
                body = stringResource(
                    R.string.plan_info_step2_body,
                    genderLabel(userDetails.gender),
                    breakdown?.aiFloorMl ?: aiFloorForGender(userDetails.gender)
                )
            )
            InfoStep(
                title = stringResource(R.string.plan_info_step3_title),
                body = stringResource(
                    R.string.plan_info_step3_body,
                    if (breakdown?.elderlyAdjustmentApplied == true)
                        stringResource(R.string.plan_info_step3_elderly) else "",
                    breakdown?.goalMl ?: aiFloorForGender(userDetails.gender)
                )
            )

            InfoStep(
                title = stringResource(R.string.plan_info_schedule_title),
                body = stringResource(R.string.plan_info_schedule_body)
            )

            Text(
                text = stringResource(R.string.plan_info_sources_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            SourceLink(
                label = stringResource(R.string.plan_info_source_iom),
                url = stringResource(R.string.plan_info_source_iom_url),
                onOpen = { openUrl(context, it) }
            )
            SourceLink(
                label = stringResource(R.string.plan_info_source_nap_news),
                url = stringResource(R.string.plan_info_source_nap_news_url),
                onOpen = { openUrl(context, it) }
            )
            SourceLink(
                label = stringResource(R.string.plan_info_source_mayo),
                url = stringResource(R.string.plan_info_source_mayo_url),
                onOpen = { openUrl(context, it) }
            )
            SourceLink(
                label = stringResource(R.string.plan_info_source_pubmed),
                url = stringResource(R.string.plan_info_source_pubmed_url),
                onOpen = { openUrl(context, it) }
            )
            SourceLink(
                label = stringResource(R.string.plan_info_source_acsm),
                url = stringResource(R.string.plan_info_source_acsm_url),
                onOpen = { openUrl(context, it) }
            )

            Text(
                text = stringResource(R.string.plan_info_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun step1Body(breakdown: HydrationPlan.GoalBreakdown?, units: Units): String {
    if (breakdown == null) return "—"
    return if (units == Units.LBS_OZ) {
        val lb = breakdown.weightKg / 0.45359237
        stringResource(R.string.plan_info_step1_body_lb, lb, breakdown.weightKg, breakdown.weightBasedMl)
    } else {
        stringResource(R.string.plan_info_step1_body, breakdown.weightKg, breakdown.weightBasedMl)
    }
}

@Composable
private fun InfoStep(title: String, body: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SourceLink(
    label: String,
    url: String,
    onOpen: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onOpen(url) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = url.removePrefix("https://").removePrefix("http://"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

private fun genderLabel(gender: Gender): String = when (gender) {
    Gender.MALE -> "men"
    Gender.FEMALE -> "women"
    Gender.OTHER -> "adults"
}

private fun aiFloorForGender(gender: Gender): Int = when (gender) {
    Gender.MALE -> 3700
    Gender.FEMALE -> 2700
    Gender.OTHER -> 3200
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}

@Composable
private fun GoalCard(
    breakdown: HydrationPlan.GoalBreakdown,
    unitLabel: String,
    gender: Gender,
    onApplyGoal: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.plan_daily_goal),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${breakdown.goalMl}",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
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
            Spacer(Modifier.height(12.dp))
            val genderLabel = when (gender) {
                Gender.MALE -> "male"
                Gender.FEMALE -> "female"
                Gender.OTHER -> "avg"
            }
            Text(
                text = stringResource(R.string.plan_goal_mlkg, breakdown.weightBasedMl),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(R.string.plan_goal_ai_floor, genderLabel, breakdown.aiFloorMl),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (breakdown.elderlyAdjustmentApplied) {
                Text(
                    text = stringResource(R.string.plan_goal_elderly),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onApplyGoal,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.plan_apply_goal))
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    schedule: List<HydrationPlan.ScheduleSlot>,
    wakeTime: LocalTime,
    sleepTime: LocalTime,
    unitLabel: String,
    onEnableReminders: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.plan_schedule_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.plan_schedule_sub,
                    wakeTime.format(timeFormatter),
                    sleepTime.format(timeFormatter),
                    60
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            schedule.forEach { slot ->
                ScheduleRow(
                    time = slot.time.format(timeFormatter),
                    amount = "${slot.amountMl} $unitLabel"
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onEnableReminders,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.AccessTime, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.plan_enable_reminders))
            }
        }
    }
}

@Composable
private fun ScheduleRow(time: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.size(14.dp))
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 16.dp)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyProfileNotice() {
    Spacer(Modifier.height(48.dp))
    Text(
        text = stringResource(R.string.plan_need_profile),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
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

/**
 * On API 31+, setExactAndAllowWhileIdle needs SCHEDULE_EXACT_ALARM granted by
 * the user. If missing, route them to the system settings screen and return
 * false so we don't write the alarms yet.
 */
private fun ensureExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return true
    if (alarmManager.canScheduleExactAlarms()) return true

    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
    Toast.makeText(context, R.string.plan_exact_alarm_hint, Toast.LENGTH_LONG).show()
    return false
}
