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

package com.criticalay.neer.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromDate(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    @JvmStatic
    fun toDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
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
}
