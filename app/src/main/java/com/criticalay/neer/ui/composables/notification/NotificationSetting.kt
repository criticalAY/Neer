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

package com.criticalay.neer.ui.composables.notification

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.criticalay.neer.R
import com.criticalay.neer.ui.composables.settings.SettingItem

@Composable
fun NotificationSetting(
    modifier: Modifier = Modifier,
    title:String,
    checked:Boolean,
    onCheckChanged: (checked:Boolean) -> Unit
){
    val notificationsEnabledState = if (checked) {
        stringResource(R.string.cd_notifications_enabled)
    } else stringResource(R.string.cd_notifications_disabled)

    SettingItem(modifier=modifier) {
        Row(
            modifier = Modifier
                .toggleable(
                    value = checked,
                    onValueChange = onCheckChanged,
                    role = Role.Switch
                )
                .semantics {
                    stateDescription = notificationsEnabledState
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier
                    .weight(1f),
                fontSize = 18.sp
            )
            Switch(checked = checked, onCheckedChange = null)
        }
    }
}

@Composable
@Preview
fun NotificationSettingsPreview(){
    NotificationSetting( title = "Enable Notification", checked = false, onCheckChanged = { })
}