package com.reguerta.presentation.device

import android.os.Build
import com.reguerta.domain.model.DeviceSnapshot
import com.reguerta.presentation.BuildConfig

object DeviceSnapshotFactory {
    fun create(deviceId: String): DeviceSnapshot {
        return DeviceSnapshot(
            deviceId = deviceId,
            platform = "android",
            manufacturer = Build.MANUFACTURER.takeIf { it.isNotBlank() },
            model = Build.MODEL.takeIf { it.isNotBlank() },
            osVersion = Build.VERSION.RELEASE.takeIf { it.isNotBlank() },
            apiLevel = Build.VERSION.SDK_INT,
            appVersion = BuildConfig.VERSION_NAME
        )
    }
}
