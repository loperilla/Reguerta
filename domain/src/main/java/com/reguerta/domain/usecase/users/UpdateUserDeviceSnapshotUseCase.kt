package com.reguerta.domain.usecase.users

import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.domain.model.DeviceSnapshot
import com.reguerta.domain.model.toDataModel
import javax.inject.Inject

class UpdateUserDeviceSnapshotUseCase @Inject constructor(
    private val usersService: UsersCollectionService
) {
    suspend operator fun invoke(userId: String, snapshot: DeviceSnapshot) {
        usersService.upsertDeviceSnapshot(userId, snapshot.toDataModel())
    }
}
