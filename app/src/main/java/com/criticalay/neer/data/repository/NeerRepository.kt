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
import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.Units
import com.criticalay.neer.data.model.User
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@ViewModelScoped
class NeerRepository @Inject constructor(
    private val userDao: UserDao,
    private val intakeDao: IntakeDao,
    private val beverageDao: BeverageDao
) {
    // ---- User ---- //
    fun getUserDetails(): Flow<User>{
        return userDao.getUserDetails()
    }

    suspend fun getUser(): User? = userDao.getUser()

    suspend fun addUser(user: User) {
        userDao.addUser(user = user)
    }

    suspend fun updateUserName(name:String){
        userDao.updateUserName(name)
    }

    suspend fun updateUserAge(age: Int) {
        userDao.updateUserAge(age)
    }

    suspend fun updateUserWeight(weight: Double) {
        userDao.updateUserWeight(weight)
    }

    suspend fun updateUserHeight(height: Double) {
        userDao.updateUserHeight(height)
    }

    suspend fun updateUserGender(gender: Gender) {
        userDao.updateUserGender(gender)
    }

    suspend fun updateUserSleepTime(bedTime: LocalTime) {
        userDao.updateUserSleepTime(bedTime)
    }

    suspend fun updateUserWakeUpTime(wakeUpTime: LocalTime) {
        userDao.updateUserWakeUpTime(wakeUpTime)
    }

    suspend fun updateUserUnits(unit: Units) {
        userDao.updateUserUnits(unit)
    }


    suspend fun updateUser(user: User) {
        userDao.updateUser(user = user)
    }

    // ---- Beverage ---- //
    suspend fun addBeverage(beverage: Beverage) {
        beverageDao.insertBeverage(beverage = beverage)
    }

    suspend fun getTargetAmount(): Int{
        return beverageDao.getTotalIntakeAmount()
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

    suspend fun updateIntakeById(intakeId: Long, intakeAmount: Int){
        intakeDao.updateIntakeAmountById(intakeId = intakeId, intakeAmount = intakeAmount)
    }

    fun getWaterIntakesForToday(
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

    fun getTodayTotalIntake(waterBeverageId: Long, startDay: LocalDateTime,
                                    endDay: LocalDateTime): Flow<Int>{
        return intakeDao.getTotalWaterIntakeForToday(waterBeverageId = waterBeverageId,
            startDate = startDay,
            endDate = endDay)
    }

}