package com.reguerta.presentation.screen.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.users.DeleteUsersUseCase
import com.reguerta.domain.usecase.users.GetAllUsersUseCase
import com.reguerta.domain.usecase.users.ToggleAdminUseCase
import com.reguerta.domain.usecase.users.ToggleProducerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.users
 * Created By Manuel Lopera on 6/2/24 at 17:09
 * All rights reserved 2024
 */
@HiltViewModel
class UserScreenViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val deleteUsersUseCase: DeleteUsersUseCase,
    private val toggleProducerUseCase: ToggleProducerUseCase,
    private val toggleAdminUseCase: ToggleAdminUseCase
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
                    getAllUsersUseCase()
                        .catch { exception ->
                            exception.printStackTrace()
                            _state.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                        .collectLatest { result ->
                            _state.update {
                                it.copy(
                                    userList = result,
                                    isLoading = false
                                )
                            }
                        }
                }


                is UserScreenEvent.ToggleProducer -> {
                    val userToggled = _state.value.userList.single { it.id == event.idToggled }
                    toggleProducerUseCase.invoke(event.idToggled, !userToggled.isProducer)
                }

                is UserScreenEvent.ToggleAdmin -> {
                    val userToggled = _state.value.userList.single { it.id == event.idToggled }
                    toggleAdminUseCase(event.idToggled, !userToggled.isAdmin)
                }

                UserScreenEvent.ConfirmDelete -> {
                    deleteUsersUseCase(state.value.idToDelete).fold(
                        onSuccess = {
                            _state.update {
                                it.copy(
                                    showAreYouSure = false,
                                    idToDelete = ""
                                )
                            }
                        },
                        onFailure = {
                            _state.update {
                                it.copy(
                                    showAreYouSure = false,
                                    idToDelete = ""
                                )
                            }
                        }
                    )
                }

                UserScreenEvent.HideAreYouSureDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSure = false,
                            idToDelete = ""
                        )
                    }
                }

                is UserScreenEvent.ShowAreYouSureDialog -> {
                    _state.update {
                        it.copy(
                            showAreYouSure = true,
                            idToDelete = event.idToDelete
                        )
                    }
                }
            }
        }
    }
}