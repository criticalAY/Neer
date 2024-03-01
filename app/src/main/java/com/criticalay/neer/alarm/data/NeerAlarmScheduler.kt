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

package com.criticalay.neer.alarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.criticalay.neer.alarm.AlarmReceiver
import com.criticalay.neer.alarm.AlarmScheduler
import timber.log.Timber
import java.time.ZoneId

class NeerAlarmScheduler(
    val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        Timber.d("Scheduling alarm")
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("NOTIFICATION_TITLE", item.title)
            putExtra("NOTIFICATION_MESSAGE", item.message)
        }

        if (item.interval!=null){
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            5 * 1000,
            PendingIntent.getBroadcast(
                context,
                101,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )
            }
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

}