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
