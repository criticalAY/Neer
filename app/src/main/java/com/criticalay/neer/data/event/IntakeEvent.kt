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

package com.criticalay.neer.data.event

import com.criticalay.neer.data.model.Intake
import java.time.LocalDateTime

sealed class IntakeEvent {

    data class AddIntake(val intake:Intake) : IntakeEvent()

    data class GetTodayIntake(val startDay: LocalDateTime, val endDay:LocalDateTime): IntakeEvent()

    data class GetTodayTotalIntake(val startDay: LocalDateTime, val endDay:LocalDateTime): IntakeEvent()

    data class DeleteIntake(val intake: Intake):IntakeEvent()

    // TODO: add them
//    data object DeleteIntake:IntakeEvent()
//    data object UpdateIntake:IntakeEvent()
}