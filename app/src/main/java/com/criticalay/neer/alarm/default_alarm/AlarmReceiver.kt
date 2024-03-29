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

package com.criticalay.neer.alarm.default_alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.criticalay.neer.alarm.default_alarm.data.AlarmDao
import com.criticalay.neer.notification.NeerNotificationService
import com.criticalay.neer.notification.NotificationItem
import com.criticalay.neer.utils.AppUtils
import com.criticalay.neer.utils.PreferencesManager
import com.criticalay.neer.utils.SleepCycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver:BroadcastReceiver() {

    @Inject
    lateinit var alarmDao: AlarmDao

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("onReceive:: getting intent")
        if (intent != null) {
            val notificationType = intent.getStringExtra("NOTIFICATION_TYPE") ?: return
            val alarmId = intent.getLongExtra("ALARM_ID", -1L)

            Timber.d("Intent not null, creating notification")
            val notificationItem = NotificationItem(
                title = AppUtils.getRandomTitle(context),
                message = AppUtils.getRandomMessage(context)
            )
            val currentTime = LocalTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES)
            val userSleepTime =  PreferencesManager(context).getSleepCycleTime(SleepCycle.SLEEP_TIME)
            val userWakeTime = PreferencesManager(context).getSleepCycleTime(SleepCycle.WAKE_TIME)

            if (currentTime.isAfter(userSleepTime) || currentTime.isBefore(userWakeTime)) {
                Timber.d("Skipping notification")
                return
            }

            val notificationService = NeerNotificationService(context)
            if (notificationType=="regular"){
                Timber.d("Regular type notification")
                notificationService.showNotification(notificationItem)
            } else{
                Timber.d("Custom type notification with alarmId: %d", alarmId)
                if (alarmId>=0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        Timber.d("Toggling alarm state to off")
                        alarmDao.toggleAlarmState(alarmId, false)
                    }
                }
                notificationService.showCustomNotification(notificationItem)
            }
        }
    }
}