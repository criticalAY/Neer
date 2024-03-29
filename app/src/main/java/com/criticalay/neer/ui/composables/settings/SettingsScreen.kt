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

package com.criticalay.neer.ui.composables.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.criticalay.neer.BuildConfig
import com.criticalay.neer.R
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.User
import com.criticalay.neer.data.repository.NeerRepository
import com.criticalay.neer.ui.composables.SectionSpacer
import com.criticalay.neer.ui.composables.settings.items.AppVersionSettingItem
import com.criticalay.neer.ui.composables.settings.items.BedTime
import com.criticalay.neer.ui.composables.settings.items.Gender
import com.criticalay.neer.ui.composables.settings.items.Height
import com.criticalay.neer.ui.composables.settings.items.NameDisplay
import com.criticalay.neer.ui.composables.settings.items.PrivacyPolicy
import com.criticalay.neer.ui.composables.settings.items.Support
import com.criticalay.neer.ui.composables.settings.items.TargetAmount
import com.criticalay.neer.ui.composables.settings.items.Units
import com.criticalay.neer.ui.composables.settings.items.WakeUpTime
import com.criticalay.neer.ui.composables.settings.items.Weight
import com.criticalay.neer.ui.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userDetails : User,
    waterDrinkTarget : Int,
    neerEventListener: (neerEvent:NeerEvent) -> Unit,
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
                neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.GetUserDetails))
            }

            Column {
                SectionSpacer(
                    modifier = Modifier.fillMaxWidth(),
                     title = stringResource(R.string.personal)
                )
                userDetails.name?.let {
                    NameDisplay(userName = it) { name ->
                        Timber.d("User updated name")
                        neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserName(name)))
                    }
                }

                HorizontalDivider()

                Gender(userGender = userDetails.gender.genderValue) { gender ->
                    Timber.d("user updated gender")
                    neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserGender(gender)))
                }

                HorizontalDivider()

                Height(userHeight = userDetails.height) { height ->
                    Timber.d("User updated height")
                    neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserHeight(height = height)))
                }

                HorizontalDivider()

                Weight(userWeight = userDetails.weight, selectedUnits = userDetails.unit) { weight ->
                    Timber.d("User updated weight")
                    neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserWeight(weight)))
                }

                HorizontalDivider()

                TargetAmount(targetAmount = waterDrinkTarget, selectedUnits = userDetails.unit ) {newTarget->
                    Timber.d("User updated drink target")
                    neerEventListener(NeerEvent.TriggerBeverageEvent(BeverageEvent.UpdateTarget(newTarget)))

                }

                HorizontalDivider()

                Units(userSelectedUnits = userDetails.unit.unitValue) {units->
                    Timber.d("User updated weight")
                    neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserUnits(units)))
                }

                SectionSpacer(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.sleep_cycle)
                )

                userDetails.wakeUpTime?.let {
                    WakeUpTime(userWakeTime = it) { time->
                        neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserWakeUpTime(time)))
                    }
                }

                HorizontalDivider()

                userDetails.bedTime?.let {
                    BedTime(userBedTime = it) { time->
                        neerEventListener(NeerEvent.TriggerUserEvent(UserEvent.UpdateUserSleepTime(time)))
                    }
                }

                SectionSpacer(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.others)
                )

                Support()

                HorizontalDivider()

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


@Composable
@Preview(showBackground = true)
fun PreviewSettingsScreen(){
    SettingsScreen(
        userDetails = User("Ashish"),
        waterDrinkTarget = 5000,
        neerEventListener = {},
        onBack = { /*TODO*/ }) {
        
    }
}