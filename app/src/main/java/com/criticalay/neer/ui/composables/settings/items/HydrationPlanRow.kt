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

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingsRow

@Composable
fun HydrationPlanRow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    SettingsRow(
        icon = Icons.Filled.WaterDrop,
        title = stringResource(R.string.hydration_plan_row),
        subtitle = stringResource(R.string.hydration_plan_row_sub),
        showChevron = true,
        onClick = onClick,
        modifier = modifier
    )
}
