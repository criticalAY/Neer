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
    val bedTime: LocalTime?= null,
    val wakeUpTime: LocalTime?=null,
    val unit: Units = Units.KG_ML,
    @PrimaryKey
    val id: Long = 100
)