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

package com.criticalay.neer.alarm.default_alarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.criticalay.neer.utils.Constants.ALARM_DATABASE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM $ALARM_DATABASE_TABLE")
    fun getAllAlarms(): Flow<List<AlarmItem>>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlarm(alarmItem: AlarmItem)

    @Update
    suspend fun updateAlarm(alarmItem: AlarmItem)

    @Delete
    suspend fun deleteAlarm(alarmItem: AlarmItem)

    @Query("UPDATE $ALARM_DATABASE_TABLE SET alarmState = :state WHERE alarmId = :alarmId")
    suspend fun toggleAlarmState(alarmId: Long, state: Boolean)
}