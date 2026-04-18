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

package com.criticalay.neer.ui.composables.home.hero

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.ui.composables.progressbar.CustomCircularProgressIndicator
import com.criticalay.neer.utils.Converters

/**
 * The central "how much water have I drunk today" visual. Combines the
 * progress ring, a wave-fill glass, and large animated numeric text. The
 * numeric value counts up smoothly when new intake is logged.
 */
@Composable
fun HydrationHero(
    modifier: Modifier = Modifier,
    todayIntake: Int,
    targetIntake: Int,
    selectedUnits: Units,
) {
    val percent = if (targetIntake <= 0) 0 else (todayIntake * 100 / targetIntake).coerceAtMost(999)
    val progressFraction = if (targetIntake <= 0) {
        0f
    } else {
        (todayIntake.toFloat() / targetIntake.toFloat()).coerceIn(0f, 1f)
    }

    val animatedIntake by animateIntAsState(
        targetValue = todayIntake,
        animationSpec = tween(durationMillis = 900),
        label = "intakeCountUp",
    )

    val unitLabel = Converters.getUnitName(selectedUnits, 1)
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .aspectRatio(1f)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            WaveGlass(
                modifier = Modifier.fillMaxWidth(0.86f).aspectRatio(1f),
                progress = progressFraction,
                primaryColor = primary,
                secondaryColor = secondary,
            )
            CustomCircularProgressIndicator(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                initialValue = todayIntake,
                maxValue = targetIntake.coerceAtLeast(1),
                primaryColor = primary,
                secondaryColor = secondary,
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$animatedIntake",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(
                        R.string.intake_of_target,
                        targetIntake,
                        unitLabel,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.percent_of_goal, percent),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
