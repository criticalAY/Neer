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

package com.criticalay.neer.data.repository

import com.criticalay.neer.alarm.default_alarm.data.AlarmDao
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
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
    private val beverageDao: BeverageDao,
    private val alarmDao: AlarmDao
) {
    // ---- User ---- //
    fun getUserDetails(): Flow<User>{
        return userDao.getUserDetails()
    }

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

    fun getTargetAmount(): Flow<Int>{
        return beverageDao.getTotalIntakeAmount()
    }

    suspend fun updateIntakeTarget(target:Int){
        beverageDao.updateTotalIntakeAmount(newTotalIntakeAmount = target)
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

    // ---- Alarm ---- //
    suspend fun createAlarm(alarm : AlarmItem){
        return alarmDao.insertAlarm(alarmItem = alarm)
    }

    fun getAllAlarms(): Flow<List<AlarmItem>>? {
        return alarmDao.getAllAlarms()
    }

    suspend fun deleteAlarm(alarm : AlarmItem){
        return alarmDao.deleteAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: AlarmItem){
        return alarmDao.updateAlarm(alarm)
    }

    suspend fun toggleAlarm(alarmId:Long, state:Boolean){
        return alarmDao.toggleAlarmState(alarmId = alarmId, state = state)
    }

}