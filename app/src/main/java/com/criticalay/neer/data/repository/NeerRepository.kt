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

package com.criticalay.neer.data.repository

import com.criticalay.neer.data.dao.BeverageDao
import com.criticalay.neer.data.dao.IntakeDao
import com.criticalay.neer.data.dao.UserDao
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

@ViewModelScoped
class NeerRepository @Inject constructor(
    private val userDao: UserDao,
    private val intakeDao: IntakeDao,
    private val beverageDao: BeverageDao
) {
    // ---- User ---- //
    suspend fun getUser(): User? = userDao.getUser()

    suspend fun addUser(user: User) {
        userDao.addUser(user = user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user = user)
    }

    // ---- Beverage ---- //
    suspend fun addBeverage(beverage: Beverage) {
        beverageDao.insertBeverage(beverage = beverage)
    }

    // ---- Intake ---- //
    suspend fun addIntake(intake: Intake) {
        intakeDao.insertIntake(intake = intake)
    }

    suspend fun deleteIntake(intake: Intake) {
        intakeDao.deleteIntake(intake = intake)
    }

    suspend fun updateIntake(intake: Intake) {
        intakeDao.updateIntake(intake = intake)
    }

    suspend fun getWaterIntakesForToday(
        waterBeverageId: Long,
        startDay: LocalDateTime,
        endDay: LocalDateTime
    ): Flow<List<Intake>> {
        return intakeDao.getWaterIntakesForToday(
            waterBeverageId = waterBeverageId,
            startOfDay = startDay,
            startOfNextDay = endDay
        )
    }

    suspend fun getTodayTotalIntake(waterBeverageId: Long, startDay: LocalDateTime,
                                    endDay: LocalDateTime): Int{
        return intakeDao.getTotalWaterIntakeForToday(waterBeverageId = waterBeverageId,
            startDate = startDay,
            endDate = endDay)
    }

}