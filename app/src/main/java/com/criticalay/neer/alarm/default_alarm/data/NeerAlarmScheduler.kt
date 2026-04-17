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

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.criticalay.neer.alarm.default_alarm.AlarmReceiver
import com.criticalay.neer.alarm.default_alarm.AlarmScheduler
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Schedules water-reminder alarms through [AlarmManager].
 *
 * All firings land on [AlarmReceiver]. Exact alarms are used when the OS grants
 * the privilege (via USE_EXACT_ALARM on ≥ API 33 or SCHEDULE_EXACT_ALARM on
 * older versions); otherwise we fall back to [AlarmManager.setAndAllowWhileIdle]
 * so Doze still lets reminders fire — at the cost of OS-imposed batching.
 */
class NeerAlarmScheduler(
    val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private val regularRequestCode = REGULAR_ALARM_REQUEST_CODE

    override fun scheduleRegular(item: AlarmItem) {
        Timber.d("Scheduling regular (repeating, interval=${item.interval}h) alarm")
        if (item.interval == null) return

        val pendingIntent = regularPendingIntent()
        val firstFireMillis = item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        val intervalMillis = (item.interval * 60 * 60 * 1000).toLong()

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            firstFireMillis,
            intervalMillis,
            pendingIntent
        )
    }

    override fun scheduleOneTime(item: AlarmItem) {
        Timber.d("Scheduling custom one-time alarm id=${item.alarmId}")
        val pendingIntent = customPendingIntent(item.alarmId)
        val fireMillis = item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        scheduleExactOrAllowWhileIdle(fireMillis, pendingIntent)
    }

    override fun scheduleRepeating(item: AlarmItem) {
        Timber.d("Scheduling custom daily-repeating alarm id=${item.alarmId}")
        val pendingIntent = customPendingIntent(item.alarmId)
        val fireMillis = nextOccurrenceMillis(item.time)
        scheduleExactOrAllowWhileIdle(fireMillis, pendingIntent)
    }

    override fun scheduleIfEnabled(item: AlarmItem) {
        if (!item.alarmState) {
            Timber.d("Skipping disabled alarm id=${item.alarmId}")
            return
        }
        if (item.repeating) scheduleRepeating(item) else scheduleOneTime(item)
    }

    override fun cancel() {
        Timber.d("Cancelling regular alarm")
        alarmManager.cancel(regularPendingIntent())
    }

    override fun cancelCustomAlarm(alarmId: Long) {
        Timber.d("Cancelling custom alarm id=$alarmId")
        alarmManager.cancel(customPendingIntent(alarmId))
    }

    private fun regularPendingIntent(): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", "regular")
        }
        return PendingIntent.getBroadcast(
            context,
            regularRequestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun customPendingIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", "custom")
            putExtra("ALARM_ID", alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * On the day the user creates/toggles the alarm, [item.time] may already
     * be in the past (e.g., user enables a 08:00 reminder at 14:00). Advance
     * to the next 24h boundary so the first firing is in the future.
     */
    private fun nextOccurrenceMillis(time: LocalDateTime): Long {
        val zone = ZoneId.systemDefault()
        var next = time
        val nowMillis = System.currentTimeMillis()
        var candidate = next.atZone(zone).toEpochSecond() * 1000
        while (candidate <= nowMillis) {
            next = next.plusDays(1)
            candidate = next.atZone(zone).toEpochSecond() * 1000
        }
        return candidate
    }

    @SuppressLint("MissingPermission")
    private fun scheduleExactOrAllowWhileIdle(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        val canSchedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else true

        if (canSchedule) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            Timber.w("Exact alarms not permitted — falling back to setAndAllowWhileIdle")
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    companion object {
        private const val REGULAR_ALARM_REQUEST_CODE = 101
    }
}
