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

import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.ui.viewmodel.SharedViewModel
import com.criticalay.neer.utils.Constants
import com.criticalay.neer.utils.PreferencesManager
import java.time.LocalDateTime

class WaterNotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // TODO: we want to add the button in the notification itself to update the water amount
    }
}