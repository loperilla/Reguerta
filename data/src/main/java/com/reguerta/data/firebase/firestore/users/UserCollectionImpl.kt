package com.reguerta.data.firebase.firestore.users

import com.google.firebase.firestore.CollectionReference
import com.reguerta.data.firebase.firestore.CollectionResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
        }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun getUser(id: String): CollectionResult<UserModel> {
        val document = collection.document(id).get().await()
        return try {
            CollectionResult.Success(document.toObject(UserModel::class.java)!!)
        } catch (ex: Exception) {
            CollectionResult.Failure(Exception("User not found"))
        }
    }

    override suspend fun toggleAdmin(id: String, newValue: Boolean) {
        collection
            .document(id)
            .update("isAdmin", newValue)
            .await()
    }

    override suspend fun toggleProducer(id: String, newValue: Boolean) {
        collection
            .document(id)
            .update("isProducer", newValue)
            .await()
    }

    override suspend fun updateUser(user: UserModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: String) {
        collection
            .document(id)
            .delete()
            .await()
    }
}