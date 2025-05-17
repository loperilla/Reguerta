package com.reguerta.domain.repository

sealed class ConfigCheckResult {
    object Ok : ConfigCheckResult()
    object ForceUpdate : ConfigCheckResult()
    object RecommendUpdate : ConfigCheckResult()
    object RequireSync : ConfigCheckResult()
}