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

package com.criticalay.neer.ui.composables.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.criticalay.neer.BuildConfig
import com.criticalay.neer.data.event.SettingsEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.ui.composables.SectionSpacer
import com.criticalay.neer.ui.composables.settings.items.AppVersionSettingItem
import com.criticalay.neer.ui.composables.settings.items.BedTime
import com.criticalay.neer.ui.composables.settings.items.Gender
import com.criticalay.neer.ui.composables.settings.items.Height
import com.criticalay.neer.ui.composables.settings.items.NameDisplay
import com.criticalay.neer.ui.composables.settings.items.PrivacyPolicy
import com.criticalay.neer.ui.composables.settings.items.Units
import com.criticalay.neer.ui.composables.settings.items.WakeUpTime
import com.criticalay.neer.ui.composables.settings.items.Weight
import com.criticalay.neer.ui.viewmodel.SharedViewModel

@Composable
fun SettingsList(
    modifier: Modifier = Modifier,
    sharedViewModel: SharedViewModel
) {
    LaunchedEffect(Unit) {
        sharedViewModel.handleUserEvent(UserEvent.GetUserDetails)
    }

    val userDetails = sharedViewModel.userDetails.collectAsState().value



    Column {
        userDetails.name?.let {
            NameDisplay(userName = it) {

            }
        }

        HorizontalDivider()
        
        Gender(userGender = userDetails.gender.name) {
            
        }

        HorizontalDivider()

        Height(userHeight = userDetails.height.toString()) {

        }

        HorizontalDivider()

        Weight(userWeight = userDetails.weight.toString()) {
            
        }

        HorizontalDivider()

        Units(userSelectedUnits = userDetails.unit.unitValue) {
            
        }

        SectionSpacer(modifier = Modifier.fillMaxWidth())

        WakeUpTime(userWakeTime = userDetails.wakeUpTime.toString()) {
            
        }

        HorizontalDivider()

        BedTime(userBedTime = userDetails.bedTime.toString()) {
            
        }


        SectionSpacer(modifier = Modifier.fillMaxWidth())

        PrivacyPolicy {

        }

        HorizontalDivider()

        AppVersionSettingItem(
            modifier = Modifier.fillMaxWidth(),
            appVersion = BuildConfig.VERSION_NAME
        )
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSettingList(){
    SettingsList(sharedViewModel = viewModel())
}