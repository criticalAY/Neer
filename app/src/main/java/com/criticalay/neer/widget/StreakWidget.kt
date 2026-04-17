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
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.compose.ui.unit.dp
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.criticalay.neer.NeerActivity

/** Read-only widget showing the current consecutive-goal-hit streak. */
class StreakWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val prefs = currentState<Preferences>()
        val streak = prefs[WidgetStateKeys.StreakDays] ?: 0
        val colors = GlanceTheme.colors

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(colors.primaryContainer)
                .cornerRadius(20.dp)
                .padding(16.dp)
                .clickable(actionStartActivity<NeerActivity>()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (streak == 0) "—" else "$streak",
                style = TextStyle(
                    color = colors.onPrimaryContainer,
                    fontSize = TextUnit(44f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = if (streak == 1) "day streak" else "day streak",
                style = TextStyle(
                    color = colors.onPrimaryContainer,
                    fontSize = TextUnit(13f, TextUnitType.Sp)
                )
            )
            Spacer(GlanceModifier.height(8.dp))
            Text(
                text = "Neer",
                style = TextStyle(
                    color = colors.onPrimaryContainer,
                    fontSize = TextUnit(11f, TextUnitType.Sp),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

class StreakWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StreakWidget()
}
