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

package com.criticalay.neer.alarm.default_alarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.criticalay.neer.alarm.default_alarm.AlarmReceiver
import com.criticalay.neer.alarm.default_alarm.AlarmScheduler
import timber.log.Timber
import java.time.ZoneId

class NeerAlarmScheduler(
    val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleRegular(item: AlarmItem) {
        Timber.d("Scheduling alarm")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", "regular")
        }

        if (item.interval != null) {
            Timber.d("NeerAlarmScheduler interval received is %.2f", item.interval)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                (item.interval * 60 * 60 * 1000).toLong(),
                PendingIntent.getBroadcast(
                    context,
                    101,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun scheduleOneTime(item: AlarmItem) {
        Timber.d("Scheduling custom alarm one time repeat")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", "custom")
            putExtra("ALARM_ID", item.alarmId)
        }
        Timber.d("alarm id recieved : %d", item.alarmId)

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.alarmId.toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun scheduleRepeating(item: AlarmItem) {
        Timber.d("Scheduling custom alarm daily repeat")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", "custom")
        }
            Timber.d("NeerAlarmScheduler interval received is %.2f", item.interval)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
                (24 * 60 * 60 * 1000).toLong(),
                PendingIntent.getBroadcast(
                    context,
                    item.alarmId.toInt(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
    }

    override fun cancel() {
        Timber.d("Cancelling alarm")
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                101,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        Timber.d("Successfully cancelled alarm")
    }

    override fun cancelCustomAlarm(alarmId: Long) {
        Timber.d("Cancelling custom alarm")
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmId.toInt(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        Timber.d("Successfully cancelled custom alarm")
    }


}