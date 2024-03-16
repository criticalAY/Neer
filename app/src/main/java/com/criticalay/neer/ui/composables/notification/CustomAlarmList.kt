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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.alarm.default_alarm.data.NeerAlarmScheduler
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.NotificationEvent
import com.criticalay.neer.utils.AppUtils
import com.criticalay.neer.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlarmList(
    modifier: Modifier = Modifier,
    allNotifications: List<AlarmItem>,
    neerEventListener: (neerEvent: NeerEvent) -> Unit
) {
    val context = LocalContext.current
    val scheduler = NeerAlarmScheduler(context = context)
    val lazyListState = rememberLazyListState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var defaultTime by remember {
        mutableStateOf(LocalTime.now())
    }
    var timeState = rememberTimePickerState(
        initialHour = defaultTime.hour,
        initialMinute = defaultTime.minute
    )

    var selectedNotification by remember {
        mutableStateOf(
            AlarmItem(
                time = LocalDateTime.now(),
                title = AppUtils.getRandomTitle(context),
                message = AppUtils.getRandomMessage(context)
            )
        )
    }
    var repeating by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(allNotifications) {
        lazyListState.animateScrollToItem(0)
    }
    if (allNotifications.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                modifier = Modifier
                    .sizeIn(100.dp, 100.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.no_custom_alarm),
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(12.dp),
                text = "No custom notification scheduled"
            )

        }
    } else {
        LazyColumn(state = lazyListState, modifier = modifier.padding(top = 10.dp)) {
            items(allNotifications,
                key = { alarmId ->
                    alarmId.alarmId
                }) { alarm ->
                val time = TimeUtils.formatLocalDateTimeToTime(alarm.time)
                CustomNotificationItem(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    checked = alarm.alarmState,
                    onCheckChanged = { state ->
                        if (!state) {
                            scheduler.cancelCustomAlarm(alarm.alarmId)
                            neerEventListener(
                                NeerEvent.TriggerNotificationEvent(
                                    NotificationEvent.ToggleNotificationState(
                                        alarmId = alarm.alarmId,
                                        state = state
                                    )
                                )
                            )
                        } else {
                            if (repeating) {

                                scheduler.scheduleRepeating(alarm)
                            } else {
                                scheduler.scheduleOneTime(alarm)
                            }
                            neerEventListener(
                                NeerEvent.TriggerNotificationEvent(
                                    NotificationEvent.ToggleNotificationState(
                                        alarmId = alarm.alarmId,
                                        state = true
                                    )
                                )
                            )
                        }
                    },
                    alarmRepeatable = (
                            if (alarm.repeating) {
                                repeating = true
                                "Repeating"
                            } else {
                                repeating = false
                                "Once"
                            }
                            ),
                    time = time,
                    longClick = {
                        selectedNotification = alarm
                        defaultTime = LocalTime.of(alarm.time.hour, alarm.time.minute)
                        showBottomSheet = true
                    }
                )
            }
        }

        if (showBottomSheet) {

            val time = rememberTimePickerState(
                initialHour = defaultTime.hour,
                initialMinute = defaultTime.minute
            )
            AlarmBottomSheet(timeState = time,
                title = stringResource(R.string.edit_notification),
                onConfirm = { selectedTime ->
                    scheduler.cancelCustomAlarm(selectedNotification.alarmId)
                    selectedNotification = selectedNotification.copy(
                        time = LocalDateTime.of(LocalDate.now(), selectedTime),
                        repeating = repeating
                    )
                    Timber.d("updating custom notification")
                    if (repeating) {
                        scheduler.scheduleRepeating(selectedNotification)
                    } else {
                        scheduler.scheduleOneTime(selectedNotification)
                    }
                    neerEventListener(
                        NeerEvent.TriggerNotificationEvent(
                            NotificationEvent.UpdateNotification(selectedNotification)
                        )
                    )
                },
                showBottomSheet = { state ->
                    showBottomSheet = state
                },
                repeatable = { value ->
                    repeating = value
                },
                deleteListener = {
                    Timber.d("Cancelling and deleting notification")
                    showBottomSheet = false
                    scheduler.cancelCustomAlarm(selectedNotification.alarmId)
                    neerEventListener(
                        NeerEvent.TriggerNotificationEvent(
                            NotificationEvent.DeleteNotification(
                                selectedNotification
                            )
                        )
                    )
                })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CustomAlarmListPreview(
    modifier: Modifier = Modifier
) {
    CustomAlarmList(allNotifications = emptyList(), neerEventListener = {})
}