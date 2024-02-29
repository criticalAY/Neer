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