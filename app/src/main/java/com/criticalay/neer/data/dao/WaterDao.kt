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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.criticalay.neer.data.model.Water
import com.criticalay.neer.utils.Constants
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface WaterDao {
    @Insert
    suspend fun insertWater(water: Water)

    @Query("DELETE FROM ${Constants.WATER_DATABASE_TABLE} WHERE waterId = :waterId")
    suspend fun deleteWaterById(waterId: Long)

    @Query("SELECT * FROM ${Constants.WATER_DATABASE_TABLE} WHERE userId = :userId ORDER BY date")
    fun getAllWaterEntries(userId: Long): Flow<List<Water>>

    @Query("SELECT * FROM ${Constants.WATER_DATABASE_TABLE} WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getWaterEntriesInDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): Flow<List<Water>>

    @Query("SELECT * FROM ${Constants.WATER_DATABASE_TABLE} WHERE userId = :userId AND date = :date")
    fun getWaterEntryForDate(userId: Long, date: LocalDate): Water?

    @Query("SELECT * FROM ${Constants.WATER_DATABASE_TABLE} WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLastWaterEntry(userId: Long): Water?
}
