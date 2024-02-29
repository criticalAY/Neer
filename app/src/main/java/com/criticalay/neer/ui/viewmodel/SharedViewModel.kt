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

package com.criticalay.neer.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.criticalay.neer.data.event.BeverageEvent
import com.criticalay.neer.data.event.IntakeEvent
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.event.UserEvent
import com.criticalay.neer.data.model.Beverage
import com.criticalay.neer.data.model.Intake
import com.criticalay.neer.data.model.User
import com.criticalay.neer.data.repository.NeerRepository
import com.criticalay.neer.utils.Constants.BEVERAGE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: NeerRepository
) : ViewModel() {

    val uiState = MutableStateFlow(User())

    private val _todayAllIntakes = MutableStateFlow<List<Intake>>(emptyList())
    val todayAllIntakes: StateFlow<List<Intake>> = _todayAllIntakes

    private val _todayTotalIntake = MutableStateFlow<Int>(0)
    val todayTotalIntake: StateFlow<Int> = _todayTotalIntake

    private val _targetIntakeAmount = MutableStateFlow<Int>(0)
    val targetIntakeAmount: StateFlow<Int> = _targetIntakeAmount

    private val _userDetails = MutableStateFlow(User())
    val userDetails : StateFlow<User> = _userDetails


    fun handleUserEvent(userEvent: UserEvent){
        when(userEvent){
            UserEvent.GetUserDetails -> {
                viewModelScope.launch {
                    repository.getUserDetails().collect{user ->
                        _userDetails.value = user
                    }
                }
            }

            is UserEvent.UpdateDetails -> {
                viewModelScope.launch {
                    repository.updateUser(userEvent.user)
                }
            }
        }
    }



    fun handleBeverageEvent(beverageEvent: BeverageEvent) {
        when (beverageEvent) {
            is BeverageEvent.AddBeverage -> {
                addBeverage(beverage = beverageEvent.beverage)
            }

            BeverageEvent.GetTargetAmount -> {
                viewModelScope.launch {
                    _targetIntakeAmount.value = repository.getTargetAmount()
                }
            }
        }

    }


    fun handleIntakeEvent(intakeEvent: IntakeEvent) {
        when (intakeEvent) {
            is IntakeEvent.AddIntake -> {
                viewModelScope.launch {
                    repository.addIntake(intakeEvent.intake)
                }
            }

            is IntakeEvent.GetTodayIntake -> {
                viewModelScope.launch {
                    repository.getWaterIntakesForToday(
                        waterBeverageId = BEVERAGE_ID,
                        startDay = intakeEvent.startDay,
                        endDay = intakeEvent.endDay
                    ).collect {
                        _todayAllIntakes.value = it
                    }
                }
            }

            is IntakeEvent.GetTodayTotalIntake -> {
                viewModelScope.launch {
                    repository.getTodayTotalIntake(
                        waterBeverageId = BEVERAGE_ID,
                        startDay = intakeEvent.startDay,
                        endDay = intakeEvent.endDay
                    ).collect{
                        _todayTotalIntake.value = it
                    }
                }
            }

            is IntakeEvent.DeleteIntake -> {
                viewModelScope.launch {
                    repository.deleteIntake(intakeEvent.intake)
                }
            }
        }
    }

    fun handleEvent(contentEvent: NeerEvent) {
        when (contentEvent) {
            is NeerEvent.AddUser -> {
                addUser(contentEvent.user)
            }

            is NeerEvent.GetUser -> {
                getUser()
            }

            is NeerEvent.UpdateUser -> {
                updateUser(contentEvent.user)
            }

            NeerEvent.Notification -> {
                // TODO: add notification system
            }
        }
    }

    private fun addBeverage(beverage: Beverage) {
        viewModelScope.launch {
            repository.addBeverage(beverage)
        }

    }

    fun getUser() {
        viewModelScope.launch {
            val user = repository.getUser()
            if (user != null) {
                uiState.value = user
            }
        }
    }


    fun addUser(user: User) {
        viewModelScope.launch {
            repository.addUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }
}