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

package com.criticalay.neer.ui.composables.settings.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Units

@Composable
fun Units(
    modifier: Modifier = Modifier,
    userSelectedUnits: String,
    newUnits: (value: Units) -> Unit,
) {
    Box {
        var expanded by remember { mutableStateOf(false) }
        com.criticalay.neer.ui.composables.settings.SettingsRow(
            icon = R.drawable.ic_bubble,
            title = stringResource(R.string.units),
            trailingValue = userSelectedUnits,
            onClick = { expanded = true },
            modifier = modifier,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            for (unit in Units.entries) {
                DropdownMenuItem(
                    text = { Text(text = unit.unitValue) },
                    onClick = {
                        newUnits(unit)
                        expanded = false
                    },
                    Modifier.offset(16.dp, 0.dp),
                )
            }
        }
    }
}
