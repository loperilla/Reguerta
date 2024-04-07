package com.reguerta.data.firebase.firestore.users

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.reguerta.data.firebase.firestore.NAME
import com.reguerta.data.firebase.firestore.USER_EMAIL
import com.reguerta.data.firebase.firestore.USER_IS_ADMIN
import com.reguerta.data.firebase.firestore.USER_IS_PRODUCER
import com.reguerta.localdata.datastore.COMPANY_NAME_KEY
import com.reguerta.localdata.datastore.IS_ADMIN_KEY
import com.reguerta.localdata.datastore.IS_PRODUCER_KEY
import com.reguerta.localdata.datastore.NAME_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.SURNAME_KEY
import com.reguerta.localdata.datastore.UID_KEY
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
    private val collection: CollectionReference,
    private val dataStore: ReguertaDataStore
) : UsersCollectionService {
    override suspend fun getUserList(): Flow<Result<List<UserModel>>> = callbackFlow {
        val subscription = collection
            .orderBy(
                NAME, Query.Direction.ASCENDING
            )
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    trySend(Result.failure(error))
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
                    trySend(Result.success(userList))
                }
            }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun getUser(id: String): Result<UserModel> {
        val document = collection.document(id).get().await()
        return try {
            Result.success(document.toObject(UserModel::class.java)!!)
        } catch (ex: Exception) {
            Result.failure(Exception("User not found"))
        }
    }

    override suspend fun saveLoggedUserInfo(email: String) {
        val snapshot = collection
            .whereEqualTo(
                USER_EMAIL,
                email
            )
            .get()
            .await()
        val documentSearch = snapshot.documents.firstOrNull()
        var user = documentSearch?.toObject(UserModel::class.java)
        user = user?.copy(id = documentSearch?.id)
        user?.let { model ->
            model.id?.let {
                dataStore.saveStringValue(
                    UID_KEY, it
                )
            }
            model.companyName?.let { company ->
                dataStore.saveStringValue(
                    COMPANY_NAME_KEY, company
                )
            }
            dataStore.saveBooleanValue(
                IS_ADMIN_KEY, model.isAdmin
            )
            dataStore.saveBooleanValue(
                IS_PRODUCER_KEY, model.isProducer
            )

            model.name?.let { name ->
                dataStore.saveStringValue(
                    NAME_KEY, name
                )
            }

            model.surname?.let {
                dataStore.saveStringValue(
                    SURNAME_KEY, it
                )
            }
        }
    }

    override suspend fun toggleAdmin(id: String, newValue: Boolean) {
        collection
            .document(id)
            .update(USER_IS_ADMIN, newValue)
            .await()
    }

    override suspend fun toggleProducer(id: String, newValue: Boolean) {
        collection
            .document(id)
            .update(USER_IS_PRODUCER, newValue)
            .await()
    }

    override suspend fun updateUser(id: String, user: UserModel) {
        collection
            .document(id)
            .set(user)
            .await()
    }

    override suspend fun deleteUser(id: String) {
        collection
            .document(id)
            .delete()
            .await()
    }

    override suspend fun addUser(user: UserModel): Result<Unit> {
        return try {
            collection
                .add(user)
                .await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}