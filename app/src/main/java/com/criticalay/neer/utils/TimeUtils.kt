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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtils {
    fun formatLocalDateTimeToTime(dateTime: LocalDateTime): String {
        val time = dateTime.toLocalTime()
        val formattedTime = DateTimeFormatter.ofPattern("hh:mm a").format(time)
        return formattedTime
    }

    fun formatTime(hour: Int, minute: Int, isAm: Boolean): String {
        val formattedHour = if (hour > 12) hour - 12 else hour
        val amPm = if (hour >= 12) "PM" else "AM"
        return String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, if (isAm) "AM" else "PM")
    }

}