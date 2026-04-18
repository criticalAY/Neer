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
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.utils.Constants.USER_ID
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime
import timber.log.Timber

/**
 * Logs a water intake when the user taps a quick-add button in the widget.
 *
 * We can't rely solely on [WidgetUpdater.refresh], because Glance action
 * callbacks run in a short window and Room's Flow-emit timing isn't
 * guaranteed within that window. So we update THIS widget's visible state
 * optimistically (bump todayIntakeMl by the tap amount), push the new state
 * into DataStore + redraw this widget, then fire the broader refresh which
 * reconciles every other widget with the authoritative DB totals.
 */
class QuickAddAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val amount = parameters[AmountKey] ?: return
        if (amount <= 0) return

        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java,
        )
        val repository = entryPoint.repository()
        val widgetUpdater = entryPoint.widgetUpdater()

        Timber.d("Widget QuickAddAction: logging %d ml", amount)

        // 1) Optimistic bump on this widget's own state so the UI updates instantly.
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[WidgetStateKeys.TodayIntakeMl] ?: 0
            prefs[WidgetStateKeys.TodayIntakeMl] = current + amount
        }
        TrackingWidget().update(context, glanceId)

        // 2) Commit the intake to Room.
        repository.addIntake(
            Intake(
                userId = USER_ID,
                beverageId = BEVERAGE_ID,
                intakeAmount = amount,
                intakeDateTime = LocalDateTime.now(),
            ),
        )

        // 3) Reconcile every widget instance with authoritative data (streak + any
        //    other open widget instances also get refreshed here).
        widgetUpdater.refresh()
    }

    companion object {
        val AmountKey = ActionParameters.Key<Int>("quick_add_amount_ml")
    }
}
