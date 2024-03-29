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

package com.criticalay.neer.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    @JvmStatic
    fun fromTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toTime(timeStr: String?): LocalTime? {
        return timeStr?.let { LocalTime.parse(it) }
    }

    interface UnitProvider {
        val unitValue: String
    }

    inline fun <reified T> getUnitName(unit: T, index: Int): String where T : Enum<T>, T : UnitProvider {
        val unitValue = (unit as UnitProvider).unitValue
        return unitValue.split("/")[index]
    }

}
