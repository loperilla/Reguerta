package com.reguerta.domain.repository

import com.google.firebase.Timestamp

data class ConfigModel(
    val lastTimestamps: Map<String, Timestamp> = emptyMap(),
    val versions: Map<String, VersionInfo> = emptyMap(),
    val cacheExpirationMinutes: Int = 60,
    val otherConfig: Map<String, Any?> = emptyMap<String, Any>()
)