package com.reguerta.data.firebase.firestore.users

import com.reguerta.data.firebase.firestore.CollectionResult
import kotlinx.coroutines.flow.Flow

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.users
 * Created By Manuel Lopera on 8/2/24 at 16:10
 * All rights reserved 2024
 */
interface UsersCollectionService {
    suspend fun getUserList(): Flow<CollectionResult<List<UserModel>>>
    suspend fun getUser(id: String): CollectionResult<UserModel>
    suspend fun toggleAdmin(id: String, newValue: Boolean)
    suspend fun toggleProducer(id: String, newValue: Boolean)
    suspend fun updateUser(user: UserModel)
    suspend fun deleteUser(id: String)
}