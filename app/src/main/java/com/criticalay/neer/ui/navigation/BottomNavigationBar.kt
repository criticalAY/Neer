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

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.criticalay.neer.R

private data class TabSpec(
    val destination: Destination,
    val labelRes: Int,
    @DrawableRes val iconRes: Int,
)

private val tabs = listOf(
    TabSpec(
        destination = Destination.HomeScreen,
        labelRes = R.string.tab_home,
        iconRes = R.drawable.ic_home,
    ),
    TabSpec(
        destination = Destination.Stats,
        labelRes = R.string.tab_stats,
        iconRes = R.drawable.ic_stats,
    ),
    TabSpec(
        destination = Destination.Settings,
        labelRes = R.string.tab_settings,
        iconRes = R.drawable.ic_settings,
    ),
)

@Composable
fun NeerBottomNavigationBar(
    currentRoute: String,
    onTabSelect: (Destination) -> Unit,
) {
    NavigationBar {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.destination.path
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelect(tab.destination) },
                icon = {
                    Icon(
                        painter = painterResource(id = tab.iconRes),
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(tab.labelRes)) },
                alwaysShowLabel = true,
            )
        }
    }
}
