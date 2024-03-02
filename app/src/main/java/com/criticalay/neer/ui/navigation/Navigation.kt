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