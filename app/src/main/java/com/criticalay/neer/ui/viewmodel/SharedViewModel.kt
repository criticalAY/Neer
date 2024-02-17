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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.criticalay.neer.data.event.NeerEvent
import com.criticalay.neer.data.model.User
import com.criticalay.neer.data.repository.NeerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: NeerRepository
) : ViewModel() {

    val uiState = MutableStateFlow(User())

    fun handleEvent(contentEvent: NeerEvent){
        when(contentEvent){
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

    fun getUser(){
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

    fun updateUser(user: User){
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }
}