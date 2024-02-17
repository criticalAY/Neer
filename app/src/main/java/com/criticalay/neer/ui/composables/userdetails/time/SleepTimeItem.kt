/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.criticalay.neer.ui.composables.userdetails.time

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem


@Composable
fun SleepTimeItem(
    modifier: Modifier = Modifier,
    title: String,
    setTime : String,
    onSettingClicked: () -> Unit,
) {
    SettingItem(modifier = modifier) {
        Row(
            modifier = with(Modifier) {
                clickable(
                    onClickLabel = stringResource(R.string.select_sleep_time)
                ) {
                    onSettingClicked()
                }
                    .padding(horizontal = 16.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f),
                fontSize = 18.sp
            )
            Text(
                text = setTime,
                fontSize = 18.sp
            )
        }
    }
}