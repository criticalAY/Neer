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
import androidx.room.Update
import com.criticalay.neer.data.model.Intake
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface IntakeDao {

    // Method to insert a new water intake entry
    @Insert
    suspend fun insertIntake(intake: Intake)

    // Method to delete a water intake entry
    @Delete
    suspend fun deleteIntake(intake: Intake)

    // Method to update a water intake entry
    @Update
    suspend fun updateIntake(intake: Intake)

    // Method to get water intake entries for today
    @Query("SELECT * FROM intake WHERE beverageId = :waterBeverageId AND intakeDateTime >= :startOfDay AND intakeDateTime < :startOfNextDay ORDER BY intakeId DESC")
    fun getWaterIntakesForToday(waterBeverageId: Long, startOfDay: LocalDateTime, startOfNextDay: LocalDateTime): Flow<List<Intake>>


    // Method to get water intake entries for a specific date range
    @Query("SELECT * FROM intake WHERE beverageId = :waterBeverageId AND DATE(intakeDateTime) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    fun getWaterIntakesForDateRange(waterBeverageId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Intake>>

    // Method to get the total amount of water intake for today
    @Query("SELECT SUM(intakeAmount) FROM intake WHERE beverageId = :waterBeverageId AND intakeDateTime >= :startDate AND intakeDateTime < :endDate ")
    fun getTotalWaterIntakeForToday(waterBeverageId: Long, startDate: LocalDateTime, endDate: LocalDateTime): Flow<Int>

    // Method to get monthly average intake amount for a specific month
    @Query("SELECT AVG(intakeAmount) FROM intake WHERE beverageId = :beverageId AND strftime('%Y-%m', intakeDateTime) = :month")
    suspend fun getMonthlyAverageIntake(beverageId: Long, month: String): Double?

    // Method to get weekly average intake amount for a specific week
    @Query("SELECT AVG(intakeAmount) FROM intake WHERE beverageId = :beverageId AND strftime('%W', intakeDateTime) = :week")
    suspend fun getWeeklyAverageIntake(beverageId: Long, week: String): Double?

}
