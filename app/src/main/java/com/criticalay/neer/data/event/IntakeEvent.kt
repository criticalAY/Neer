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

import com.criticalay.neer.data.model.Intake
import java.time.LocalDateTime

sealed class IntakeEvent {

    data class AddIntake(val intake:Intake) : IntakeEvent()

    data class GetTodayIntake(val startDay: LocalDateTime, val endDay:LocalDateTime): IntakeEvent()

    data class GetTodayTotalIntake(val startDay: LocalDateTime, val endDay:LocalDateTime): IntakeEvent()

    data class DeleteIntake(val intake: Intake):IntakeEvent()

    data class UpdateIntakeById(val intakeId:Long, val intakeAmount:Int) : IntakeEvent()

    // TODO: add them
//    data object DeleteIntake:IntakeEvent()
//    data object UpdateIntake:IntakeEvent()
}