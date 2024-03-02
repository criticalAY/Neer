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
import androidx.room.Query
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.utils.Constants.BEVERAGE_DATABASE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface BeverageDao {
    @Insert
    suspend fun insertBeverage(beverage: Beverage)

    @Query("SELECT totalIntakeAmount FROM $BEVERAGE_DATABASE_TABLE")
    suspend fun getTotalIntakeAmount(): Int


}