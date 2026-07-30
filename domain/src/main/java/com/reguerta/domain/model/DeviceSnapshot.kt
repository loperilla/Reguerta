package com.reguerta.domain.model

import com.google.firebase.Timestamp
import com.reguerta.data.firebase.firestore.devicesnapshot.DeviceSnapshotModel

data class DeviceSnapshot(
    val deviceId: String,
    val platform: String,
    val manufacturer: String? = null,
    val model: String? = null,
    val osVersion: String? = null,
    val apiLevel: Int? = null,
    val appVersion: String? = null,
    val firstSeenAt: Timestamp? = null,
    val lastSeenAt: Timestamp? = null
)

fun DeviceSnapshotModel.toDomain(): DeviceSnapshot {
    return DeviceSnapshot(
        deviceId = deviceId.orEmpty(),
        platform = platform ?: "unknown",
        manufacturer = manufacturer,
        model = model,
        osVersion = osVersion,
        apiLevel = apiLevel,
        appVersion = appVersion,
        firstSeenAt = firstSeenAt,
        lastSeenAt = lastSeenAt
    )
}

fun DeviceSnapshot.toDataModel(): DeviceSnapshotModel {
    return DeviceSnapshotModel(
        deviceId = deviceId,
        platform = platform,
        manufacturer = manufacturer,
        model = model,
        osVersion = osVersion,
        apiLevel = apiLevel,
        appVersion = appVersion,
        firstSeenAt = firstSeenAt,
        lastSeenAt = lastSeenAt
    )
}
