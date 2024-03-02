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

package com.criticalay.neer.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("NeerPrefs", Context.MODE_PRIVATE)

    fun saveWaterDetails() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("water_details_filled", true)
        editor.apply()
    }

    fun isWaterDetailsFilled(): Boolean {
        return sharedPreferences.getBoolean("water_details_filled", false)
    }

    fun saveUserDetails() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("user_details_filled", true)
        editor.apply()
    }

    fun isUserDetailsFilled(): Boolean {
        return sharedPreferences.getBoolean("user_details_filled", false)
    }

    fun setWaterAmount(value:Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("selected_water_amount", value)
        editor.apply()
    }

    fun getWaterAmount(): Int {
        return sharedPreferences.getInt("selected_water_amount", 100)
    }

    fun saveNotificationPreference(value:Boolean){
        val editor = sharedPreferences.edit()
        editor.putBoolean("notification_pref", value)
        editor.apply()
    }

    fun getNotificationPreference():Boolean{
        return sharedPreferences.getBoolean("notification_pref", false)
    }

}