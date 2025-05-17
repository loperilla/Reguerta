package com.reguerta.domain.usecase.config

import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.repository.ConfigRepository

class GetConfigUseCase(
    private val repository: ConfigRepository
) {
    suspend operator fun invoke(): ConfigModel {
        return repository.getGlobalConfig()
    }
}