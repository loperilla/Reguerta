package com.reguerta.presentation.screen.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.firebase.firestore.CollectionResult
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.presentation.model.User
import com.reguerta.presentation.model.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 6/2/24 at 17:09
 * All rights reserved 2024
 */
@HiltViewModel
class UserScreenViewModel @Inject constructor(
    private val usersCollection: UsersCollectionService
) : ViewModel() {

    private var _state: MutableStateFlow<UserScreenState> = MutableStateFlow(UserScreenState())
    val state: StateFlow<UserScreenState> = _state
    fun onEvent(event: UserScreenEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                UserScreenEvent.GoOut -> {
                    _state.update {
                        it.copy(goOut = true)
                    }
                }

                UserScreenEvent.LoadUsers -> {
                    usersCollection.getUserList().collectLatest { result ->
                        when (result) {
                            is CollectionResult.Failure -> {
                                result.exception.printStackTrace()
                                _state.update {
                                    it.copy(
                                        isLoading = false
                                    )
                                }
                            }

                            is CollectionResult.Success -> {
                                val userDomain = mutableListOf<User>()
                                result.data.forEach {
                                    userDomain.add(it.toDomain())
                                }
                                Timber.tag("viewmodel").e(userDomain.toString())
                                _state.update {
                                    it.copy(
                                        userList = userDomain,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                }

                is UserScreenEvent.ToggleProducer -> {
                    val userToggled = _state.value.userList.single { it.id == event.idToggled }
                    usersCollection.toggleProducer(event.idToggled, !userToggled.isProducer)
                }

                is UserScreenEvent.ToggleAdmin -> {
                    val userToggled = _state.value.userList.single { it.id == event.idToggled }
                    usersCollection.toggleAdmin(event.idToggled, !userToggled.isAdmin)
                }

                is UserScreenEvent.DeleteUser -> {
                    usersCollection.deleteUser(event.idToDelete)
                }

                is UserScreenEvent.EditUser -> {

                }
            }
        }
    }
}