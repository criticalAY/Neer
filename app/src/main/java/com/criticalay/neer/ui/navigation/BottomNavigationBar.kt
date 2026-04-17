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

package com.criticalay.neer.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.criticalay.neer.R

private data class TabSpec(
    val destination: Destination,
    val labelRes: Int,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

private val tabs = listOf(
    TabSpec(
        destination = Destination.HomeScreen,
        labelRes = R.string.tab_home,
        iconSelected = Icons.Filled.Home,
        iconUnselected = Icons.Outlined.Home
    ),
    TabSpec(
        destination = Destination.Stats,
        labelRes = R.string.tab_stats,
        iconSelected = Icons.Rounded.BarChart,
        iconUnselected = Icons.Outlined.BarChart
    ),
    TabSpec(
        destination = Destination.Settings,
        labelRes = R.string.tab_settings,
        iconSelected = Icons.Filled.Settings,
        iconUnselected = Icons.Outlined.Settings
    )
)

@Composable
fun NeerBottomNavigationBar(
    currentRoute: String,
    onTabSelect: (Destination) -> Unit
) {
    NavigationBar {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.destination.path
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelect(tab.destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.iconSelected else tab.iconUnselected,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(tab.labelRes)) },
                alwaysShowLabel = true
            )
        }
    }
}
