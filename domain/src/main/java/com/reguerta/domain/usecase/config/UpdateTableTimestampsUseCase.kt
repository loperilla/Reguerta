package com.reguerta.domain.usecase.config

import com.reguerta.domain.repository.ConfigRepository
import javax.inject.Inject

class UpdateTableTimestampsUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    suspend operator fun invoke(table: String) {
        configRepository.updateTableTimestamp(table)
    }
}