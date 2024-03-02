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

    @Query("UPDATE intake SET intakeAmount = :intakeAmount WHERE intakeId = :intakeId")
    suspend fun updateIntakeAmountById(intakeId: Long, intakeAmount: Int)

}
