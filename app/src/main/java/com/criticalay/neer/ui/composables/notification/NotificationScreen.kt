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

package com.criticalay.neer.ui.composables.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.criticalay.neer.R
import com.criticalay.neer.alarm.data.NeerAlarmScheduler
import com.criticalay.neer.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var switchState by remember {
        mutableStateOf(PreferencesManager(context).getNotificationPreference())
    }
    // State for showing/hiding the alert dialog
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notification),
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
    )  {padding ->
        Column(modifier = modifier.padding(padding)) {
            NotificationSetting(
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    ),
                title = stringResource(R.string.setting_enable_notifications),
                checked = switchState,
                onCheckChanged = {checked ->
                    if (!checked) {
                        showDialog = true
                    } else {
                        switchState = true
                        PreferencesManager(context).saveNotificationPreference(true)
                        NeerAlarmScheduler(context = context).cancel()
                    }
                }
            )

            if (showDialog){
                AlertDialogNotification(
                    onDismissRequest = {
                        showDialog = false
                                       },
                    onConfirmation = {
                        showDialog = false
                        switchState = false
                        PreferencesManager(context).saveNotificationPreference(false)
                        NeerAlarmScheduler(context = context).cancel()
                    },
                    dialogTitle = stringResource(R.string.disable_notifications),
                    dialogText = stringResource(R.string.notification_disable_message),
                    icon = Icons.Default.Warning
                )
            }
        }
    }
}