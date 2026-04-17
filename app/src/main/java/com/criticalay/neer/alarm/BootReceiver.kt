/*
 * Copyright (c) 2026 Ashish Yadav <mailtoashish693@gmail.com>
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
import com.criticalay.neer.alarm.default_alarm.AlarmScheduler
import com.criticalay.neer.data.repository.NeerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Re-hydrates every enabled [com.criticalay.neer.alarm.default_alarm.data.AlarmItem]
 * with [AlarmScheduler] after the device boots or the app is upgraded, since
 * AlarmManager drops all alarms on reboot and APK replacement.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: NeerRepository
    @Inject lateinit var scheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action !in HANDLED_ACTIONS) return

        Timber.d("BootReceiver received $action — rescheduling alarms")
        val pending = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                val alarms = repository.getAllAlarmsSnapshot()
                Timber.d("Restoring ${alarms.size} alarm(s)")
                alarms.filter { it.alarmState }.forEach { scheduler.scheduleIfEnabled(it) }
            } catch (t: Throwable) {
                Timber.e(t, "Failed to restore alarms on boot")
            } finally {
                pending.finish()
            }
        }
    }

    private companion object {
        val HANDLED_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON"
        )
    }
}
