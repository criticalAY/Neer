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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.utils.Converters
import kotlinx.coroutines.launch

private val quickAddPresets = listOf(100, 150, 200, 250, 300, 400, 500, 750)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    initialAmount: Int,
    selectedUnits: Units,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var amount by remember { mutableIntStateOf(initialAmount.coerceAtLeast(50)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.add_water),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.quick_add_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            val rows = quickAddPresets.chunked(4)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { preset ->
                        PresetChip(
                            amount = preset,
                            isSelected = preset == amount,
                            selectedUnits = selectedUnits,
                            modifier = Modifier.weight(1f),
                            onSelect = {
                                haptics.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove,
                                )
                                amount = preset
                            },
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.custom_amount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Slider(
                value = amount.toFloat(),
                onValueChange = { amount = it.toInt() },
                valueRange = 50f..1000f,
                steps = 18,
            )
            Text(
                text = "$amount ${Converters.getUnitName(selectedUnits, 1)}",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onConfirm(amount)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_water_full),
                    contentDescription = null,
                    tint = Color.White,
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(
                        R.string.add_amount,
                        "$amount ${Converters.getUnitName(selectedUnits, 1)}",
                    ),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PresetChip(
    amount: Int,
    isSelected: Boolean,
    selectedUnits: Units,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
) {
    val container = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(container)
            .clickable { onSelect() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_juice_glass),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$amount",
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
        )
        Text(
            text = Converters.getUnitName(selectedUnits, 1),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = 0.8f),
        )
    }
}
