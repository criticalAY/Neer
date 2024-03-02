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

package com.criticalay.neer.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.criticalay.neer.notification.NeerNotificationService
import com.criticalay.neer.notification.NotificationItem
import timber.log.Timber

class AlarmReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Timber.d("onReceive:: getting intent")
        if (intent != null) {
            val title = intent.getStringExtra("NOTIFICATION_TITLE") ?: return
            val message = intent.getStringExtra("NOTIFICATION_MESSAGE") ?: return

            Timber.d("Intent not null, creating notification")
            val notificationItem = NotificationItem(
                title = title,
                message = message
            )
            val notificationService = NeerNotificationService(context)
            notificationService.showNotification(notificationItem)
        }
    }
}