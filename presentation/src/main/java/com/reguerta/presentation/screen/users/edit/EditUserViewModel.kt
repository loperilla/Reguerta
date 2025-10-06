package com.reguerta.presentation.screen.users.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.usecase.config.UpdateTableTimestampsUseCase
import com.reguerta.domain.usecase.users.EditUserUseCase
import com.reguerta.domain.usecase.users.GetUserByIdUseCase
import com.reguerta.presentation.checkAllStringAreNotEmpty
import com.reguerta.presentation.type.isValidEmail
import com.reguerta.domain.enums.TypeConsumerUser
import com.reguerta.domain.enums.TypeProducerUser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

/*****
 * Project: Reguerta
 * From: com.reguerta.presentation.screen.edit_user
 * Created By Manuel Lopera on 24/2/24 at 13:03
 * All rights reserved 2024
 */

@HiltViewModel(assistedFactory = EditUserViewModelFactory::class)
class EditUserViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    private val getUserUseCase: GetUserByIdUseCase,
    private val editUserUseCase: EditUserUseCase,
    private val updateTableTimestampsUseCase: UpdateTableTimestampsUseCase
) : ViewModel() {
    private var _state: MutableStateFlow<EditUserState> = MutableStateFlow(EditUserState())
    val state: StateFlow<EditUserState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getUserUseCase(userId).fold(onSuccess = { userDomain ->
                _state.update {
                    it.copy(
                        name = userDomain.name,
                        surname = userDomain.surname,
                        email = userDomain.email,
                        companyName = userDomain.companyName,
                        isAdmin = userDomain.isAdmin,
                        phoneNumber = userDomain.phone,
                        isProducer = userDomain.isProducer,
                        typeProducer = userDomain.typeProducer,
                        typeConsumer = userDomain.typeConsumer,
                        numResignations = userDomain.numResignations,
                        available = userDomain.available,
                        tropical1 = userDomain.tropical1,
                        tropical2 = userDomain.tropical2
                    )
                }
            }, onFailure = {
                it.printStackTrace()
            })
        }
    }

    fun onEvent(event: EditUserEvent) {
        viewModelScope.launch {
            when (event) {
                is EditUserEvent.CompanyNameInputChanges -> {
                    _state.update {
                        it.copy(companyName = event.inputValue)
                    }
                }

                EditUserEvent.EditUser -> {
                    with(_state.value) {
                        editUserUseCase.invoke(
                            id = userId,
                            name = name,
                            surname = surname,
                            phoneNumber = phoneNumber,
                            email = email,
                            isAdmin = isAdmin,
                            isProducer = isProducer,
                            companyName = companyName,
                            typeProducer = typeProducer,
                            typeConsumer = typeConsumer,
                            numResignations = numResignations,
                            available = available,
                            tropical1 = tropical1,
                            tropical2 = tropical2
                        ).fold(onSuccess = {
                            updateTableTimestampsUseCase("users")
                            _state.update {
                                it.copy(goOut = true)
                            }
                        }, onFailure = {
                            Timber.tag("EditUserViewModel").e(it)
                        })
                    }
                }

                is EditUserEvent.EmailInputChanges -> {
                    _state.update {
                        it.copy(email = event.inputValue)
                    }
                }

                EditUserEvent.GoBack -> _state.update { it.copy(goOut = true) }
                is EditUserEvent.NameInputChanges -> _state.update {
                    it.copy(name = event.inputValue)
                }

                is EditUserEvent.SurnameInputChanges -> _state.update {
                    it.copy(surname = event.inputValue)
                }

                is EditUserEvent.ToggledIsAdmin -> _state.update {
                    it.copy(isAdmin = event.newValue)
                }

                is EditUserEvent.ToggledIsProducer -> _state.update {
                    val newIsProducer = event.newValue

                    val currentProducerEnum = it.typeProducer.asProducerEnumOrNull()
                    val currentConsumerEnum = it.typeConsumer.asConsumerEnumOrNull()

                    val newProducerEnum: TypeProducerUser? = when {
                        newIsProducer && currentProducerEnum == null -> TypeProducerUser.REGULAR
                        newIsProducer -> currentProducerEnum
                        else -> null // no es productor ⇒ representamos con string vacío
                    }

                    val newConsumerEnum: TypeConsumerUser = when {
                        // Productor NORMAL ⇒ consumidor NONE
                        newIsProducer && newProducerEnum == TypeProducerUser.REGULAR -> TypeConsumerUser.NONE
                        // Productor COMPRAS ⇒ conservar paridad; si venía NONE/invalid ⇒ REGULAR
                        newIsProducer && newProducerEnum == TypeProducerUser.SHOP -> when (currentConsumerEnum) {
                            null, TypeConsumerUser.NONE -> TypeConsumerUser.REGULAR
                            else -> currentConsumerEnum
                        }
                        // Si deja de ser productor ⇒ no tocar (si estaba inválido, normalizamos a REGULAR)
                        else -> currentConsumerEnum ?: TypeConsumerUser.REGULAR
                    }

                    it.copy(
                        isProducer = newIsProducer,
                        available = newIsProducer,
                        typeProducer = newProducerEnum?.raw() ?: "",
                        typeConsumer = newConsumerEnum.raw()
                    )
                }

                is EditUserEvent.ToggledIsShoppingProducer -> _state.update {
                    val newProducerEnum = if (event.newValue) TypeProducerUser.SHOP else TypeProducerUser.REGULAR
                    val currentConsumerEnum = it.typeConsumer.asConsumerEnumOrNull()

                    val newConsumerEnum: TypeConsumerUser = if (it.isProducer) {
                        if (newProducerEnum == TypeProducerUser.REGULAR) {
                            // Productor NORMAL ⇒ consumidor NONE
                            TypeConsumerUser.NONE
                        } else {
                            // Productor COMPRAS ⇒ conservar paridad; si venía NONE/invalid ⇒ REGULAR
                            when (currentConsumerEnum) {
                                null, TypeConsumerUser.NONE -> TypeConsumerUser.REGULAR
                                else -> currentConsumerEnum
                            }
                        }
                    } else {
                        // Si no es productor, no tocar; si inválido, REGULAR
                        currentConsumerEnum ?: TypeConsumerUser.REGULAR
                    }

                    it.copy(
                        typeProducer = newProducerEnum.raw(),
                        typeConsumer = newConsumerEnum.raw()
                    )
                }

                is EditUserEvent.PhoneNumberInputChanges -> _state.update {
                    it.copy(phoneNumber = event.inputValue)
                }
            }
            if (event !is EditUserEvent.GoBack && event !is EditUserEvent.EditUser) {
                _state.update { it.copy(isButtonEnabled = checkButtonEnabled()) }
            }
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

private fun String.asConsumerEnumOrNull(): TypeConsumerUser? =
    TypeConsumerUser.entries.firstOrNull { it.value == this }

private fun String.asProducerEnumOrNull(): TypeProducerUser? =
    TypeProducerUser.entries.firstOrNull { it.value == this }

private fun TypeConsumerUser.raw(): String = this.value
private fun TypeProducerUser.raw(): String = this.value