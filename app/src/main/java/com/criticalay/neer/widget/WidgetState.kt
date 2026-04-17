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

package com.criticalay.neer.widget

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Keys shared between [WidgetUpdater], the widget composables, and the
 * configuration activity. Each widget instance has its own preferences store
 * (Glance default), so the [QuickAddAmount*] keys are effectively per-instance.
 */
object WidgetStateKeys {
    val TodayIntakeMl = intPreferencesKey("today_intake_ml")
    val TargetIntakeMl = intPreferencesKey("target_intake_ml")
    val StreakDays = intPreferencesKey("streak_days")
    val UnitLabel = stringPreferencesKey("unit_label")
    val UserName = stringPreferencesKey("user_name")

    val QuickAddAmount1 = intPreferencesKey("quick_add_amount_1")
    val QuickAddAmount2 = intPreferencesKey("quick_add_amount_2")
}
