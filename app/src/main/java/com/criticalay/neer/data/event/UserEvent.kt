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

package com.criticalay.neer.data.event

import com.criticalay.neer.data.model.Gender
import com.criticalay.neer.data.model.Units
import java.time.LocalTime


sealed class UserEvent {
    data object GetUserDetails : UserEvent()

    data class UpdateUserName(val name: String) : UserEvent()

    data class UpdateUserAge(val age: Int) : UserEvent()

    data class UpdateUserWeight(val weight: Double) : UserEvent()

    data class UpdateUserHeight(val height: Double) :UserEvent()

    data class UpdateUserGender(val gender: Gender) :UserEvent()

    data class UpdateUserSleepTime(val bedTime: LocalTime) : UserEvent()

    data class UpdateUserWakeUpTime(val wakeUpTime: LocalTime) : UserEvent()

    data class UpdateUserUnits(val unit: Units) : UserEvent()
}