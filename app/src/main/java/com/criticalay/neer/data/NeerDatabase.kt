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

package com.criticalay.neer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.criticalay.neer.data.dao.BeverageDao
import com.criticalay.neer.data.dao.IntakeDao
import com.criticalay.neer.data.dao.UserDao
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.utils.Converters

@Database(entities = [Beverage::class, User::class, Intake::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class NeerDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun intakeDao() : IntakeDao
    abstract fun beverageDao() : BeverageDao
}