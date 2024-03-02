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

package com.criticalay.neer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.data.model.User
import com.criticalay.neer.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

@Dao
interface UserDao {
    @Query("SELECT * FROM ${Constants.USER_DATABASE_TABLE}")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM ${Constants.USER_DATABASE_TABLE}")
    fun getUserDetails() : Flow<User>

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET name = :name")
    suspend fun updateUserName(name: String)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET age = :age")
    suspend fun updateUserAge(age: Int)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET weight = :weight")
    suspend fun updateUserWeight(weight: Double)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET height = :height")
    suspend fun updateUserHeight(height: Double)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET gender = :gender")
    suspend fun updateUserGender(gender: Gender)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET bedTime = :bedTime")
    suspend fun updateUserSleepTime(bedTime: LocalTime)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET wakeUpTime = :wakeUpTime")
    suspend fun updateUserWakeUpTime(wakeUpTime: LocalTime)

    @Query("UPDATE ${Constants.USER_DATABASE_TABLE} SET unit = :unit")
    suspend fun updateUserUnits(unit: Units)
}
