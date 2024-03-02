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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.criticalay.neer.BuildConfig
import com.criticalay.neer.R
import com.criticalay.neer.data.dao.BeverageDao
import com.criticalay.neer.data.dao.IntakeDao
import com.criticalay.neer.data.dao.UserDao
import com.criticalay.neer.data.event.SettingsEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.repository.NeerRepository
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
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    sharedViewModel: SharedViewModel,
    onBack : () -> Unit,
    onPrivacy:() -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )

                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(padding)
        ) {
            LaunchedEffect(Unit) {
                sharedViewModel.handleUserEvent(UserEvent.GetUserDetails)
            }

            val userDetails = sharedViewModel.userDetails.collectAsState().value

            Column {
                userDetails.name?.let {
                    NameDisplay(userName = it) { name ->
                        Timber.d("User updated name")
                        sharedViewModel.handleUserEvent(UserEvent.UpdateUserName(name))
                    }
                }

                HorizontalDivider()

                Gender(userGender = userDetails.gender.genderValue) { gender ->
                    Timber.d("user updated gender")
                    sharedViewModel.handleUserEvent(UserEvent.UpdateUserGender(gender))
                }

                HorizontalDivider()

                Height(userHeight = userDetails.height) { height ->
                    Timber.d("User updated height")
                    sharedViewModel.handleUserEvent(UserEvent.UpdateUserHeight(height = height))
                }

                HorizontalDivider()

                Weight(userWeight = userDetails.weight) { weight ->
                    Timber.d("User updated weight")
                    sharedViewModel.handleUserEvent(UserEvent.UpdateUserWeight(weight))
                }

                HorizontalDivider()

                Units(userSelectedUnits = userDetails.unit.unitValue) {units->
                    Timber.d("User updated weight")
                    sharedViewModel.handleUserEvent(UserEvent.UpdateUserUnits(units))
                }

                SectionSpacer(modifier = Modifier.fillMaxWidth())

                userDetails.wakeUpTime?.let {
                    WakeUpTime(userWakeTime = it) { time->
                        sharedViewModel.handleUserEvent(UserEvent.UpdateUserWakeUpTime(time))
                    }
                }

                HorizontalDivider()

                userDetails.bedTime?.let {
                    BedTime(userBedTime = it) { time->
                        sharedViewModel.handleUserEvent(UserEvent.UpdateUserSleepTime(time))
                    }
                }

                SectionSpacer(modifier = Modifier.fillMaxWidth())

                PrivacyPolicy {
                    onPrivacy()
                }

                HorizontalDivider()

                AppVersionSettingItem(
                    modifier = Modifier.fillMaxWidth(),
                    appVersion = BuildConfig.VERSION_NAME
                )
            }


        }
    }

}