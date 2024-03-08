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
import com.criticalay.neer.data.model.User
import java.time.LocalTime

sealed class NeerEvent {
    class AddUser(val user: User) : NeerEvent()

    data class TriggerIntakeEvent(val intakeEvent: IntakeEvent) : NeerEvent()

    data class TriggerUserEvent(val userEvent: UserEvent): NeerEvent()

    data class TriggerBeverageEvent(val beverageEvent: BeverageEvent) : NeerEvent()

}