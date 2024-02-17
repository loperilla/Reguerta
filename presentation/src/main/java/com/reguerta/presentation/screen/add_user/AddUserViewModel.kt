package com.reguerta.presentation.screen.add_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.data.firebase.firestore.CollectionResult
import com.reguerta.data.firebase.firestore.users.UserModel
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
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
    private val userCollectionService: UsersCollectionService
) : ViewModel() {
    private var _state: MutableStateFlow<AddUserState> = MutableStateFlow(AddUserState())
    val state: StateFlow<AddUserState> = _state

    fun onEvent(event: AddUserEvent) {
        viewModelScope.launch {
            when (event) {
                AddUserEvent.AddUser -> {
                    val userModel = UserModel(
                        name = _state.value.name,
                        surname = _state.value.surname,
                        email = _state.value.email,
                        isAdmin = _state.value.isAdmin,
                        isProducer = _state.value.isProducer,
                        companyName = _state.value.companyName
                    )
                    when (val result = userCollectionService.addUser(userModel)) {
                        is CollectionResult.Failure -> {
                            Timber.tag("AddUserViewModel").e(result.exception)

                        }

                        is CollectionResult.Success -> {
                            _state.update {
                                it.copy(goOut = true)
                            }
                        }
                    }
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
                            companyName = if (!event.newValue) "" else it.companyName
                        )
                    }
                }

                AddUserEvent.GoBack -> _state.update {
                    it.copy(goOut = true)
                }
            }
            if (event !is AddUserEvent.GoBack && event !is AddUserEvent.AddUser) {
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
            return name.isNotEmpty() && surname.isNotEmpty() && email.isValidEmail && companyCheck
        }
    }
}