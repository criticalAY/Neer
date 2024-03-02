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