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