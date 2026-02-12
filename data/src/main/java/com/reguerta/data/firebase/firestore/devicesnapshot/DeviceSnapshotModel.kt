package com.reguerta.data.firebase.firestore.devicesnapshot

import com.google.firebase.Timestamp

data class DeviceSnapshotModel(
    val deviceId: String? = null,
    val platform: String? = null,
    val manufacturer: String? = null,
    val model: String? = null,
    val osVersion: String? = null,
    val apiLevel: Int? = null,
    val appVersion: String? = null,
    val firstSeenAt: Timestamp? = null,
    val lastSeenAt: Timestamp? = null
)

fun DeviceSnapshotModel.toMap(): Map<String, Any?> {
    return mapOf(
        "deviceId" to deviceId,
        "platform" to platform,
        "manufacturer" to manufacturer,
        "model" to model,
        "osVersion" to osVersion,
        "apiLevel" to apiLevel,
        "appVersion" to appVersion,
        "firstSeenAt" to firstSeenAt,
        "lastSeenAt" to lastSeenAt
    ).filterValues { it != null }
}
