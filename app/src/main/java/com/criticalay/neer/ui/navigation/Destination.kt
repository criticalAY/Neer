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

sealed class Destination(val path: String) {
    data object HomeScreen : Destination("home")

    data object UserDetails : Destination("userDetails")

    data object WaterDetails : Destination("waterDetails")

    data object Settings : Destination("settings")

    data object Notification : Destination("notifications")

    data object Privacy : Destination("privacy")

    companion object {
        fun fromString(route: String): Destination {
            return when (route) {
                Settings.path -> Settings
                UserDetails.path -> UserDetails
                WaterDetails.path -> WaterDetails
                else -> HomeScreen
            }

        }
    }
}