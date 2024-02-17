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

package com.criticalay.neer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.criticalay.neer.utils.Constants.USER_DATABASE_TABLE
import java.time.LocalTime

@Entity(tableName = USER_DATABASE_TABLE)
data class User(
    // No id field
    val name: String?= null,
    val age: Int? = null,
    val gender: Gender = Gender.FEMALE,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val bedTime: LocalTime = LocalTime.now(),
    val wakeUpTime: LocalTime? = LocalTime.now(),
    val unit: String? = null,
    @PrimaryKey
    val id: Long = 1
) {

}