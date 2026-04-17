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

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.repository.NeerRepository
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.utils.Converters
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps every installed Neer widget instance in sync with the app's data.
 *
 * Both widgets read a small `Preferences` snapshot (today's intake, target,
 * streak, etc.) that lives in their per-widget Glance DataStore. This class
 * recomputes those values from the repository and pushes them into every
 * instance, then calls the widgets' `updateAll` to trigger a redraw.
 */
@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: NeerRepository
) {

    suspend fun refresh() {
        val snapshot = runCatching { buildSnapshot() }.getOrNull() ?: run {
            Timber.w("WidgetUpdater: no snapshot (likely pre-onboarding) — skipping")
            return
        }

        val manager = GlanceAppWidgetManager(context)
        val trackingIds = manager.getGlanceIds(TrackingWidget::class.java)
        val streakIds = manager.getGlanceIds(StreakWidget::class.java)

        trackingIds.forEach { id -> writeSharedFields(id, snapshot) }
        streakIds.forEach { id -> writeSharedFields(id, snapshot) }

        if (trackingIds.isNotEmpty()) TrackingWidget().updateAll(context)
        if (streakIds.isNotEmpty()) StreakWidget().updateAll(context)
    }

    private suspend fun writeSharedFields(id: GlanceId, snapshot: Snapshot) {
        updateAppWidgetState(context, id) { prefs: MutablePreferences ->
            prefs[WidgetStateKeys.TodayIntakeMl] = snapshot.todayIntakeMl
            prefs[WidgetStateKeys.TargetIntakeMl] = snapshot.targetIntakeMl
            prefs[WidgetStateKeys.StreakDays] = snapshot.streakDays
            prefs[WidgetStateKeys.UnitLabel] = snapshot.unitLabel
            prefs[WidgetStateKeys.UserName] = snapshot.userName.orEmpty()
        }
    }

    private suspend fun buildSnapshot(): Snapshot {
        val todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN)
        val todayEnd = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN)
        val historyStart = LocalDateTime.of(LocalDate.now().minusDays(29), LocalTime.MIN)

        val todayTotal = repository
            .getTodayTotalIntake(BEVERAGE_ID, todayStart, todayEnd)
            .first()
        val target = repository.getTargetAmount().first()
        val history = repository.getIntakeHistory(BEVERAGE_ID, historyStart, todayEnd).first()
        val user = repository.getUserDetails().first()
        val streak = computeStreak(history, target)

        return Snapshot(
            todayIntakeMl = todayTotal,
            targetIntakeMl = target,
            streakDays = streak,
            unitLabel = Converters.getUnitName(user.unit, 1),
            userName = user.name
        )
    }

    private fun computeStreak(history: List<Intake>, target: Int): Int {
        if (target <= 0) return 0
        val totalsByDate = history
            .groupBy { it.intakeDateTime.toLocalDate() }
            .mapValues { e -> e.value.sumOf { it.intakeAmount } }
        var streak = 0
        var cursor = LocalDate.now()
        while ((totalsByDate[cursor] ?: 0) >= target) {
            streak += 1
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    private data class Snapshot(
        val todayIntakeMl: Int,
        val targetIntakeMl: Int,
        val streakDays: Int,
        val unitLabel: String,
        val userName: String?
    )
}
