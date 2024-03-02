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

package com.criticalay.neer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.criticalay.neer.ui.composables.home.Home
import com.criticalay.neer.ui.composables.notification.NotificationScreen
import com.criticalay.neer.ui.composables.privacy.PrivacyScreen
import com.criticalay.neer.ui.composables.settings.SettingsScreen
import com.criticalay.neer.ui.composables.userdetails.UserDetailForm
import com.criticalay.neer.ui.composables.waterdetails.WaterDetailForm
import com.criticalay.neer.ui.viewmodel.SharedViewModel
import com.criticalay.neer.utils.PreferencesManager

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var startDestination by rememberSaveable { mutableStateOf(Destination.UserDetails.path) }

    val userDetailsFilled = preferencesManager.isUserDetailsFilled()
    val waterDetailsFilled = preferencesManager.isWaterDetailsFilled()

    val initialDestination = when {
        userDetailsFilled && waterDetailsFilled -> Destination.HomeScreen.path
        waterDetailsFilled -> Destination.HomeScreen.path
        userDetailsFilled -> Destination.WaterDetails.path
        else -> Destination.UserDetails.path
    }

    startDestination = initialDestination



    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(route = Destination.UserDetails.path) {
            UserDetailForm(neerEventListener = sharedViewModel::handleEvent,
                onProceed = {
                    preferencesManager.saveUserDetails()
                    navController.navigate(Destination.WaterDetails.path)
                })
        }

        composable(route = Destination.WaterDetails.path) {
            WaterDetailForm(beverageEventListener = sharedViewModel::handleBeverageEvent,
                onProceed = {
                    preferencesManager.saveWaterDetails()
                    navController.navigate(Destination.HomeScreen.path)
                })

        }

        composable(route = Destination.HomeScreen.path) {
            Home(
                sharedViewModel = sharedViewModel,
                navigateToSettings = {
                    navController.navigate(Destination.Settings.path)
                },
                navigateToNotifications = {
                    navController.navigate(Destination.Notification.path)
                }
            )
        }

        composable(route = Destination.Notification.path){
            NotificationScreen(
                onBack = {
                    navController.navigate(Destination.HomeScreen.path){
                        popUpTo(Destination.HomeScreen.path){
                            inclusive=true
                        }
                    }
                }
            )
        }

        composable(route = Destination.Privacy.path){
            PrivacyScreen(onBack = {
                navController.navigate(Destination.HomeScreen.path){
                    popUpTo(Destination.HomeScreen.path){
                        inclusive=true
                    }
                }
            })
        }

        composable(route = Destination.Settings.path) {
            SettingsScreen(
                sharedViewModel = sharedViewModel,
                onPrivacy = {
                    navController.navigate(Destination.Privacy.path)
                },
                onBack = {
                    navController.navigate(Destination.HomeScreen.path){
                        popUpTo(Destination.HomeScreen.path){
                            inclusive=true
                        }
                    }
                }
            )
        }


//        composable(route = Destination.Settings.path) {
//            Settings {
//                navController.navigate(Destination.Home.path) {
//                    popUpTo(navController.graph.startDestinationId) {
//                        inclusive = false
//                    }
//                    launchSingleTop = true
//                }
//            }
//        }
//        composable(route = Destination.Home.path) {
//            HomeScreen {
//                navController.navigate(Destination.Settings.path)
//            }
//        }
    }
}