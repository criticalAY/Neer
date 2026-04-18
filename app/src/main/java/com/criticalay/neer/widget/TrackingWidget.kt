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
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.criticalay.neer.NeerActivity
import com.criticalay.neer.R

/**
 * Progress widget with two configurable quick-add buttons.
 *
 * Amounts come from the widget's own Preferences (populated by
 * [TrackingWidgetConfigureActivity]); defaults are 250 ml / 500 ml.
 */
class TrackingWidget : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val prefs = currentState<Preferences>()
        val today = prefs[WidgetStateKeys.TodayIntakeMl] ?: 0
        val target = prefs[WidgetStateKeys.TargetIntakeMl] ?: 0
        val unitLabel = prefs[WidgetStateKeys.UnitLabel].orEmpty().ifEmpty { "ml" }
        val amount1 = prefs[WidgetStateKeys.QuickAddAmount1] ?: 250
        val amount2 = prefs[WidgetStateKeys.QuickAddAmount2] ?: 500
        val progress = if (target <= 0) 0f else (today.toFloat() / target).coerceIn(0f, 1f)
        val percent = (progress * 100).toInt()
        val colors = GlanceTheme.colors

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(colors.surface)
                .cornerRadius(20.dp)
                .padding(14.dp),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .clickable(actionStartActivity<NeerActivity>()),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_outline_water_full),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colors.primary),
                    modifier = GlanceModifier.size(22.dp),
                )
                Spacer(GlanceModifier.width(8.dp))
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = "$today $unitLabel",
                        style = TextStyle(
                            color = colors.onSurface,
                            fontSize = TextUnit(22f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = if (target > 0) "of $target $unitLabel · $percent%" else "Set a daily goal",
                        style = TextStyle(
                            color = colors.onSurfaceVariant,
                            fontSize = TextUnit(11f, TextUnitType.Sp),
                        ),
                    )
                }
            }
            Spacer(GlanceModifier.height(10.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = GlanceModifier.fillMaxWidth().height(6.dp),
                color = colors.primary,
                backgroundColor = colors.surfaceVariant,
            )
            Spacer(GlanceModifier.height(12.dp))
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                QuickAddButton(
                    amountMl = amount1,
                    unitLabel = unitLabel,
                    modifier = GlanceModifier.defaultWeight(),
                )
                Spacer(GlanceModifier.width(10.dp))
                QuickAddButton(
                    amountMl = amount2,
                    unitLabel = unitLabel,
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }
    }
}

@Composable
private fun QuickAddButton(
    amountMl: Int,
    unitLabel: String,
    modifier: GlanceModifier = GlanceModifier,
) {
    val colors = GlanceTheme.colors
    Row(
        modifier = modifier
            .background(colors.primary)
            .cornerRadius(14.dp)
            .padding(vertical = 12.dp, horizontal = 10.dp)
            .clickable(
                actionRunCallback<QuickAddAction>(
                    actionParametersOf(QuickAddAction.AmountKey to amountMl),
                ),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_outline_water_full),
            contentDescription = null,
            colorFilter = ColorFilter.tint(colors.onPrimary),
            modifier = GlanceModifier.size(16.dp),
        )
        Spacer(GlanceModifier.width(6.dp))
        Text(
            text = "+$amountMl $unitLabel",
            style = TextStyle(
                color = colors.onPrimary,
                fontSize = TextUnit(14f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

class TrackingWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TrackingWidget()
}
