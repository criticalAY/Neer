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

package com.criticalay.neer.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.criticalay.neer.NeerActivity
import com.criticalay.neer.R
import com.criticalay.neer.utils.Constants.WATER_REMINDER_CHANNEL_ID
import timber.log.Timber

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

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, WaterNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, WATER_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_outline_water_bottle)
            .setContentTitle(notificationItem.title)
            .setContentText(notificationItem.message)
            .setContentIntent(activityPendingIntent)
            .addAction(R.drawable.ic_outline_water_bottle,
                context.getString(R.string.drunk), actionPendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }

}