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

package com.criticalay.neer.ui.composables.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.alarm.default_alarm.data.NeerAlarmScheduler
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.NotificationEvent
import com.criticalay.neer.ui.composables.SectionSpacer
import com.criticalay.neer.ui.composables.notification.dialog.AlertDialogNotification
import com.criticalay.neer.utils.AppUtils
import com.criticalay.neer.utils.PreferencesManager
import timber.log.Timber
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    notificationList: List<AlarmItem>,
    neerEventListener: (event: NeerEvent) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scheduler = NeerAlarmScheduler(context = context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showDialog by remember { mutableStateOf(false) }
    var switchState by remember {
        mutableStateOf(PreferencesManager(context).getNotificationPreference())
    }
    var intervalTime by remember {
        mutableStateOf(
            PreferencesManager(context = context)
                .getNotificationInterval()
        )
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = 5,
        initialMinute = 50
    )
    var repeatable by remember {
        mutableStateOf(false)
    }
    var selectedDateTime = LocalDateTime.now()

    LaunchedEffect(Unit) {
        neerEventListener(NeerEvent.TriggerNotificationEvent(NotificationEvent.GetAllScheduledNotifications))
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )

                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(10.dp),
                onClick = { showBottomSheet = true },
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            NotificationSetting(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    ),
                title = stringResource(R.string.setting_enable_notifications),
                checked = switchState,
                onCheckChanged = { checked ->
                    if (!checked) {
                        Timber.d("Showing cancel notification dialog")
                        showDialog = true
                    } else {
                        Timber.d("Turning back notification on")
                        PreferencesManager(context).saveNotificationPreference(true)
                        val scheduler = NeerAlarmScheduler(context = context)
                        val alarmItem = AlarmItem(
                            LocalDateTime.now().plusMinutes(30),
                            PreferencesManager(context = context)
                                .getNotificationInterval(),
                            AppUtils.getRandomTitle(context),
                            AppUtils.getRandomMessage(context)
                        )
                        scheduler.scheduleRegular(alarmItem)
                        switchState = true
                    }
                }
            )

            HorizontalDivider()

            NotificationIntervalSetting(
                notificationInterval = intervalTime
            ) { interval ->
                Timber.d("New interval is %.2f", interval)
                val scheduler = NeerAlarmScheduler(context = context)
                scheduler.cancel()
                intervalTime = interval
                PreferencesManager(context = context).setNotificationInterval(interval)
                val alarmItem = AlarmItem(
                    LocalDateTime.now().plusMinutes(30),
                    interval,
                    AppUtils.getRandomTitle(context),
                    AppUtils.getRandomMessage(context)
                )
                scheduler.scheduleRegular(alarmItem)
            }

            SectionSpacer(
                modifier = Modifier.fillMaxWidth(),
                title = "Custom notification"
            )

            CustomAlarmList(
                allNotifications = notificationList,
                neerEventListener = neerEventListener
            )

            if (showBottomSheet) {
                AlarmBottomSheet(
                    timeState = timeState,
                    title = "New Notification",
                    onConfirm = { time ->
                        val alarm = AlarmItem(
                            time = selectedDateTime.with(time),
                            title = AppUtils.getRandomTitle(context),
                            message = AppUtils.getRandomMessage(context),
                            repeating = repeatable
                        )
                        neerEventListener(
                            NeerEvent.TriggerNotificationEvent(
                                NotificationEvent.SaveNotification(alarm)
                            )
                        )
                        val latestNotificationItem = notificationList.lastOrNull()
                        Timber.d("Setting custom notification")
                        if (latestNotificationItem != null) {
                            if (repeatable) {
                                scheduler.scheduleRepeating(latestNotificationItem)

                            } else {
                                scheduler.scheduleOneTime(latestNotificationItem)
                            }
                        }
                    },
                    showBottomSheet = { state ->
                        showBottomSheet = state
                    },
                    repeatable = { repeating ->
                        repeatable = repeating
                    }
                )
            }

            if (showDialog) {
                AlertDialogNotification(
                    onDismissRequest = {
                        Timber.d("Dismissing the notification dialog")
                        showDialog = false
                    },
                    onConfirmation = {
                        showDialog = false
                        switchState = false
                        Timber.d("User turned off Notifications")
                        PreferencesManager(context).saveNotificationPreference(false)
                        NeerAlarmScheduler(context = context).cancel()
                    },
                    dialogTitle = stringResource(R.string.disable_notifications),
                    dialogText = stringResource(R.string.notification_disable_message),
                    icon = Icons.Default.Warning
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewNotificationScreen() {
    NotificationScreen(notificationList = emptyList(), neerEventListener = {

    }) {

    }
}