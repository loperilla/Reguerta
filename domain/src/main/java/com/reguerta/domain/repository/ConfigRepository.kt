package com.reguerta.domain.repository

interface ConfigRepository {
    suspend fun getGlobalConfig(): ConfigModel
    suspend fun updateTableTimestamp(table: String)
}