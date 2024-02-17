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

import com.criticalay.neer.data.dao.UserDao
import com.criticalay.neer.data.dao.WaterDao
import com.criticalay.neer.data.model.User
import com.criticalay.neer.data.model.Water
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

@ViewModelScoped
class NeerRepository @Inject constructor(
    private val userDao: UserDao,
    private val waterDao: WaterDao
) {

    suspend fun getUser() : User? = userDao.getUser()

    suspend fun addUser(user: User) {
        userDao.addUser(user = user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user = user)
    }

    suspend fun insertWater(water: Water) {
        waterDao.insertWater(water = water)
    }

    suspend fun deleteWaterById(waterId: Long) {
        waterDao.deleteWaterById(waterId = waterId)
    }

    fun getAllWaterEntries(userId: Long): Flow<List<Water>> =
        waterDao.getAllWaterEntries(userId = userId)

    fun getWaterEntriesInDateRange(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<Water>> = waterDao.getWaterEntriesInDateRange(userId, startDate, endDate)

    fun getWaterEntryForDate(userId: Long, date: LocalDate): Water? =
        waterDao.getWaterEntryForDate(userId, date)

    fun getLastWaterEntry(userId: Long): Water? = waterDao.getLastWaterEntry(userId)

}