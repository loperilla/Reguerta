package com.reguerta.domain.repository

data class VersionInfo(
    val min: String = "",
    val current: String = "",
    val forceUpdate: Boolean = false,
    val storeUrl: String = ""
)
