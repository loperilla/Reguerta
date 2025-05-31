package com.reguerta.presentation.screen.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.usecase.config.GetConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val getConfigUseCase: GetConfigUseCase
) : ViewModel() {

    private val _config = MutableStateFlow<ConfigModel?>(null)
    val config: StateFlow<ConfigModel?> = _config.asStateFlow()

    fun loadConfig() {
        viewModelScope.launch {
            try {
                val result = getConfigUseCase()
                _config.value = result
            } catch (e: Exception) {
                e.printStackTrace()
                // Aquí podrías emitir un error state o loguearlo
            }
        }
    }
}