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

package com.criticalay.neer.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.criticalay.neer.NeerActivity
import com.criticalay.neer.R
import com.criticalay.neer.utils.Constants.WATER_REMINDER_CHANNEL_ID
import com.criticalay.neer.utils.PreferencesManager
import com.criticalay.neer.utils.SleepCycle
import timber.log.Timber
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class NeerNotificationService(
    private val context: Context
) : NeerNotification {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun showNotification(notificationItem: NotificationItem) {
            Timber.d("Showing notification")
            val activityIntent = Intent(context, NeerActivity::class.java)
            val activityPendingIntent = PendingIntent.getActivity(
                context,
                1,
                activityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, WATER_REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_outline_water_bottle)
                .setContentTitle(notificationItem.title)
                .setContentText(notificationItem.message)
                .setContentIntent(activityPendingIntent)
                .build()

            notificationManager.notify(1, notification)
    }
}