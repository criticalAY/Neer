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

package com.criticalay.neer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.criticalay.neer.alarm.default_alarm.AlarmScheduler
import com.criticalay.neer.alarm.default_alarm.data.AlarmItem
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.NotificationEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.data.repository.NeerRepository
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import com.criticalay.neer.widget.WidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: NeerRepository,
    private val alarmScheduler: AlarmScheduler,
    private val widgetUpdater: WidgetUpdater
) : ViewModel() {
    private val _todayAllIntakes = MutableStateFlow<List<Intake>>(emptyList())
    val todayAllIntakes: StateFlow<List<Intake>> = _todayAllIntakes

    private val _todayTotalIntake = MutableStateFlow<Int>(0)
    val todayTotalIntake: StateFlow<Int> = _todayTotalIntake

    private val _targetIntakeAmount = MutableStateFlow<Int>(0)
    val targetIntakeAmount: StateFlow<Int> = _targetIntakeAmount

    private val _userDetails = MutableStateFlow(User())
    val userDetails : StateFlow<User> = _userDetails

    private val _allNotifications = MutableStateFlow<List<AlarmItem>>(emptyList())
    val allNotifications: StateFlow<List<AlarmItem>> = _allNotifications

    private val _intakeHistory = MutableStateFlow<List<Intake>>(emptyList())
    val intakeHistory: StateFlow<List<Intake>> = _intakeHistory

    fun handleEvent(neerEvent: NeerEvent) {
        when (neerEvent) {
            is NeerEvent.AddUser -> {
                viewModelScope.launch {
                    repository.addUser(neerEvent.user)
                }
            }

            is NeerEvent.TriggerBeverageEvent -> {
                when (neerEvent.beverageEvent) {
                    is BeverageEvent.AddBeverage -> {
                        viewModelScope.launch {
                            repository.addBeverage(neerEvent.beverageEvent.beverage)
                        }
                    }

                    BeverageEvent.GetTargetAmount -> {
                        viewModelScope.launch {
                            repository.getTargetAmount().collect{targetIntakeAmount ->
                                _targetIntakeAmount.value = targetIntakeAmount

                            }
                        }
                    }

                    is BeverageEvent.UpdateTarget -> {
                        viewModelScope.launch {
                            repository.updateIntakeTarget(neerEvent.beverageEvent.target)
                            widgetUpdater.refresh()
                        }
                    }
                }
            }
            is NeerEvent.TriggerIntakeEvent -> {
                when (neerEvent.intakeEvent) {
                    is IntakeEvent.AddIntake -> {
                        viewModelScope.launch {
                            repository.addIntake(neerEvent.intakeEvent.intake)
                            widgetUpdater.refresh()
                        }
                    }

                    is IntakeEvent.GetTodayIntake -> {
                        viewModelScope.launch {
                            repository.getWaterIntakesForToday(
                                waterBeverageId = BEVERAGE_ID,
                                startDay = neerEvent.intakeEvent.startDay,
                                endDay = neerEvent.intakeEvent.endDay
                            ).collect {
                                _todayAllIntakes.value = it
                            }
                        }
                    }

                    is IntakeEvent.GetTodayTotalIntake -> {
                        viewModelScope.launch {
                            repository.getTodayTotalIntake(
                                waterBeverageId = BEVERAGE_ID,
                                startDay = neerEvent.intakeEvent.startDay,
                                endDay = neerEvent.intakeEvent.endDay
                            ).collect{
                                _todayTotalIntake.value = it
                            }
                        }
                    }

                    is IntakeEvent.DeleteIntake -> {
                        viewModelScope.launch {
                            repository.deleteIntake(neerEvent.intakeEvent.intake)
                            widgetUpdater.refresh()
                        }
                    }

                    is IntakeEvent.UpdateIntakeById -> {
                        viewModelScope.launch {
                            repository.updateIntakeById(intakeId = neerEvent.intakeEvent.intakeId, intakeAmount =  neerEvent.intakeEvent.intakeAmount)
                            widgetUpdater.refresh()
                        }
                    }

                    is IntakeEvent.GetIntakeHistory -> {
                        viewModelScope.launch {
                            repository.getIntakeHistory(
                                waterBeverageId = BEVERAGE_ID,
                                startDate = neerEvent.intakeEvent.startDate,
                                endDate = neerEvent.intakeEvent.endDate
                            ).collect { _intakeHistory.value = it }
                        }
                    }
                }
            }
            is NeerEvent.TriggerUserEvent -> {
                when(neerEvent.userEvent){
                    UserEvent.GetUserDetails -> {
                        viewModelScope.launch {
                            repository.getUserDetails().collect{user ->
                                _userDetails.value = user
                            }
                        }
                    }
                    is UserEvent.UpdateUserAge -> {
                        viewModelScope.launch {
                            repository.updateUserAge(neerEvent.userEvent.age)
                        }
                    }
                    is UserEvent.UpdateUserGender -> {
                        viewModelScope.launch {
                            repository.updateUserGender(neerEvent.userEvent.gender)
                        }
                    }
                    is UserEvent.UpdateUserHeight -> {
                        viewModelScope.launch {
                            repository.updateUserHeight(neerEvent.userEvent.height)
                        }
                    }
                    is UserEvent.UpdateUserName -> {
                        viewModelScope.launch {
                            repository.updateUserName(neerEvent.userEvent.name)
                        }
                    }
                    is UserEvent.UpdateUserSleepTime -> {
                        viewModelScope.launch {
                            repository.updateUserSleepTime(neerEvent.userEvent.bedTime)
                        }
                    }
                    is UserEvent.UpdateUserUnits -> {
                        viewModelScope.launch {
                            repository.updateUserUnits(neerEvent.userEvent.unit)
                        }
                    }
                    is UserEvent.UpdateUserWakeUpTime -> {
                        viewModelScope.launch {
                            repository.updateUserWakeUpTime(neerEvent.userEvent.wakeUpTime)
                        }
                    }
                    is UserEvent.UpdateUserWeight -> {
                        viewModelScope.launch {
                            repository.updateUserWeight(neerEvent.userEvent.weight)
                        }
                    }
                }
            }

            is NeerEvent.TriggerNotificationEvent -> {
                when (val event = neerEvent.notificationEvent) {
                    is NotificationEvent.DeleteNotification -> {
                        viewModelScope.launch {
                            alarmScheduler.cancelCustomAlarm(event.notification.alarmId)
                            repository.deleteAlarm(event.notification)
                        }
                    }

                    NotificationEvent.GetAllScheduledNotifications -> {
                        viewModelScope.launch {
                            repository.getAllAlarms()?.collect { alarms ->
                                _allNotifications.value = alarms
                            }
                        }
                    }

                    is NotificationEvent.SaveNotification -> {
                        viewModelScope.launch {
                            val id = repository.createAlarmReturningId(event.notification)
                            val stored = event.notification.copy(alarmId = id)
                            alarmScheduler.scheduleIfEnabled(stored)
                        }
                    }

                    is NotificationEvent.UpdateNotification -> {
                        viewModelScope.launch {
                            alarmScheduler.cancelCustomAlarm(event.notification.alarmId)
                            repository.updateAlarm(event.notification)
                            alarmScheduler.scheduleIfEnabled(event.notification)
                        }
                    }

                    is NotificationEvent.ToggleNotificationState -> {
                        viewModelScope.launch {
                            repository.toggleAlarm(event.alarmId, event.state)
                            val updated = repository.getAllAlarmsSnapshot()
                                .firstOrNull { it.alarmId == event.alarmId } ?: return@launch
                            if (event.state) {
                                alarmScheduler.scheduleIfEnabled(updated)
                            } else {
                                alarmScheduler.cancelCustomAlarm(event.alarmId)
                            }
                        }
                    }

                    is NotificationEvent.ReplaceAllAlarms -> {
                        viewModelScope.launch {
                            repository.getAllAlarmsSnapshot().forEach {
                                alarmScheduler.cancelCustomAlarm(it.alarmId)
                            }
                            repository.clearAllAlarms()
                            event.alarms.forEach { template ->
                                val id = repository.createAlarmReturningId(template)
                                alarmScheduler.scheduleIfEnabled(template.copy(alarmId = id))
                            }
                        }
                    }
                }
            }
        }
    }
}