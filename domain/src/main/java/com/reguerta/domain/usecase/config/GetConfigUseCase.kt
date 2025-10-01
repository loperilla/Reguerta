package com.reguerta.domain.usecase.config

import com.reguerta.domain.repository.ConfigModel
import com.reguerta.domain.repository.ConfigRepository
import javax.inject.Inject

class GetConfigUseCase @Inject constructor(
    private val repository: ConfigRepository
) {
    suspend operator fun invoke(): ConfigModel {
        return repository.getGlobalConfig()
    }
}