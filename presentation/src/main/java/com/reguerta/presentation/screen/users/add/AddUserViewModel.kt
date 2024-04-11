package com.reguerta.presentation.screen.users.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.users.AddUserUseCase
import com.reguerta.presentation.checkAllStringAreNotEmpty
import com.reguerta.presentation.screen.users.edit.EditUserEvent
import com.reguerta.presentation.type.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.add_user
 * Created By Manuel Lopera on 17/2/24 at 11:58
 * All rights reserved 2024
 */

@HiltViewModel
class AddUserViewModel @Inject constructor(
    private val createUserUseCase: AddUserUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<AddUserState> = MutableStateFlow(AddUserState())
    val state: StateFlow<AddUserState> = _state

    fun onEvent(event: AddUserEvent) {
        viewModelScope.launch {
            when (event) {
                AddUserEvent.AddUser -> {
                    val currentState = _state.value
                    // Establecer valores predeterminados
                    val numResignations = 0 // Siempre 0 al añadir un nuevo usuario
                    val available = true // Siempre true al añadir un nuevo usuario

                    createUserUseCase.invoke(
                        name = currentState.name,
                        surname = currentState.surname,
                        email = currentState.email,
                        phoneNumber = currentState.phoneNumber,
                        isAdmin = currentState.isAdmin,
                        isProducer = currentState.isProducer,
                        companyName = if (currentState.isProducer) currentState.companyName else "",
                        numResignations = numResignations,
                        typeConsumer = currentState.typeConsumer,
                        typeProducer = if (currentState.isProducer) currentState.typeProducer else "",
                        available = available
                    ).fold(
                        onSuccess = {
                            _state.update { it.copy(goOut = true) }
                        },
                        onFailure = {
                            Timber.tag("AddUserViewModel").e(it)
                            }
                        )
                    }

                is AddUserEvent.CompanyNameInputChanges -> {
                    _state.update {
                        it.copy(companyName = event.inputValue)
                    }
                }

                is AddUserEvent.EmailInputChanges -> {
                    _state.update {
                        it.copy(email = event.inputValue)
                    }
                }

                is AddUserEvent.NameInputChanges -> {
                    _state.update {
                        it.copy(name = event.inputValue)
                    }
                }

                is AddUserEvent.SurnameInputChanges -> {
                    _state.update {
                        it.copy(surname = event.inputValue)
                    }
                }

                is AddUserEvent.ToggledIsAdmin -> {
                    _state.update {
                        it.copy(isAdmin = event.newValue)
                    }
                }

                is AddUserEvent.ToggledIsProducer -> {
                    _state.update {
                        it.copy(
                            isProducer = event.newValue,
                            available = event.newValue,
                            companyName = if (!event.newValue) "" else it.companyName,
                            typeProducer = when {
                                event.newValue && it.typeProducer.isEmpty() -> "normal"
                                !event.newValue -> ""
                                else -> it.typeProducer
                            },
                            typeConsumer = when {
                                event.newValue && it.typeProducer == "compras" -> "normal"
                                event.newValue -> "sin"
                                else -> "normal"
                            }
                        )
                    }
                }

                is AddUserEvent.ToggledIsShoppingProducer -> _state.update {
                    val newTypeProducer = if (event.newValue) "compras" else "normal"
                    it.copy(
                        typeProducer = newTypeProducer,
                        typeConsumer = when {
                            it.isProducer && newTypeProducer == "compras" -> "normal"
                            it.isProducer -> "sin"
                            else -> it.typeConsumer
                        }
                    )
                }

                AddUserEvent.GoBack -> _state.update {
                    it.copy(goOut = true)
                }

                is AddUserEvent.PhoneNumberInputChanges -> _state.update {
                    it.copy(phoneNumber = event.inputValue)
                }
            }


            }
            if (event !is AddUserEvent.GoBack && event !is AddUserEvent.AddUser) {
                _state.update { it.copy(isButtonEnabled = checkButtonEnabled()) }
            }
        }


    private fun checkButtonEnabled(): Boolean {
        with(_state.value) {
            val companyCheck = if (isProducer) {
                companyName.isNotEmpty()
            } else {
                true
            }
            return checkAllStringAreNotEmpty(name, surname) && email.isValidEmail && companyCheck
        }
    }
}