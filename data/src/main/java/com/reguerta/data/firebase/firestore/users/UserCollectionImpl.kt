package com.reguerta.data.firebase.firestore.users

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.CollectionResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.users
 * Created By Manuel Lopera on 8/2/24 at 16:14
 * All rights reserved 2024
 */
class UserCollectionImpl @Inject constructor(
    private val collection: CollectionReference
) : UsersCollectionService {
    override suspend fun getUserList(): Flow<CollectionResult<List<UserModel>>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                trySend(CollectionResult.Failure(error))
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let { query ->
                val userList = mutableListOf<UserModel>()
                query.documents.forEach { document ->
                    val user = document.toObject(UserModel::class.java)
                    user?.let { model ->
                        model.id = document.id
                        userList.add(model)
                    }
                }
                trySend(CollectionResult.Success(userList))
            }
            if (snapshot != null) {
                val userList = snapshot.toObjects(UserModel::class.java)
                trySend(CollectionResult.Success(userList)).isSuccess
            }
        }
        awaitClose {
            subscription.remove()
        }
    }
}