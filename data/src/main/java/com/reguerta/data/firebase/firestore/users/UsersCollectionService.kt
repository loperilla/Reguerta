package com.reguerta.data.firebase.firestore.users

import com.reguerta.data.firebase.firestore.devicesnapshot.DeviceSnapshotModel
import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.users
 * Created By Manuel Lopera on 8/2/24 at 16:10
 * All rights reserved 2024
 */

interface UsersCollectionService {
    suspend fun getUserList(): Flow<Result<List<UserModel>>>
    suspend fun getUserListFromServer(): Result<List<UserModel>>
    suspend fun getUser(id: String): Result<UserModel>
    suspend fun saveLoggedUserInfo(email: String)
    suspend fun toggleAdmin(id: String, newValue: Boolean)
    suspend fun toggleProducer(id: String, newValue: Boolean)
    suspend fun updateUser(id: String, user: UserModel)
    suspend fun upsertDeviceSnapshot(userId: String, snapshot: DeviceSnapshotModel)
    suspend fun deleteUser(id: String)
    suspend fun addUser(user: UserModel): Result<Unit>
}
