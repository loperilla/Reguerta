package com.reguerta.data.firebase.firestore.users

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.reguerta.data.firebase.firestore.DEVICES
import com.reguerta.data.firebase.firestore.LAST_DEVICE_ID
import com.reguerta.data.firebase.firestore.NAME
import com.reguerta.data.firebase.firestore.USER_EMAIL
import com.reguerta.data.firebase.firestore.USER_IS_ADMIN
import com.reguerta.data.firebase.firestore.USER_IS_PRODUCER
import com.reguerta.data.firebase.firestore.devicesnapshot.DeviceSnapshotModel
import com.reguerta.data.firebase.firestore.devicesnapshot.toMap
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
    companion object {
        private const val TAG = "UserCollectionImpl"
    }
    override suspend fun getUserList(): Flow<Result<List<UserModel>>> = callbackFlow {
        Log.i(TAG, "USERS_LIST_INIT - Subscribiendo a colección de usuarios (orderBy=NAME)")
        val subscription = collection
            .orderBy(NAME, Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "USERS_LIST_ERROR - Firestore listener devolvió error", error)
                    error.printStackTrace()
                    trySend(Result.failure(error))
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { query ->
                    if (query.metadata.isFromCache && query.size() <= 1) {
                        Log.w(TAG, "USERS_LIST_SKIP_CACHE - Ignorando snapshot de cache sospechoso (size<=1); esperando servidor…")
                        return@addSnapshotListener
                    }
                    if (!query.metadata.isFromCache) {
                        Log.i(TAG, "USERS_LIST_SERVER - snapshot de servidor recibido, docs=${query.size()}")
                    }
                    Log.d(TAG, "USERS_LIST_SNAPSHOT - docs=${query.size()}, fromCache=${query.metadata.isFromCache}, pendingWrites=${query.metadata.hasPendingWrites()}")
                    val userList = mutableListOf<UserModel>()
                    query.documents.forEach { document ->
                        val user = document.toObject(UserModel::class.java)
                        user?.let { model ->
                            model.id = document.id
                            userList.add(model)
                        }
                    }
                    val producers = userList.count { it.isProducer }
                    val admins = userList.count { it.isAdmin }
                    Log.i(TAG, "USERS_LIST_EMIT - count=${userList.size}, producers=$producers, admins=$admins, sampleIds=${userList.take(10).map { it.id }}")
                    if (userList.size <= 1) {
                        Log.w(TAG, "USERS_LIST_WARN - tamaño de lista sospechoso (${userList.size}). Revisa reglas de seguridad, entorno (dev/prod) o filtros en la query.")
                    }
                    trySend(Result.success(userList))
                }
            }
        awaitClose {
            Log.i(TAG, "USERS_LIST_CLOSE - Listener desuscrito")
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
            .whereEqualTo(USER_EMAIL, email)
            .get()
            .await()
        val documentSearch = snapshot.documents.firstOrNull()
        var user = documentSearch?.toObject(UserModel::class.java)
        user = user?.copy(id = documentSearch?.id)
        user?.let { model ->
            model.id?.let {
                dataStore.saveStringValue(UID_KEY, it)
            }
            model.companyName?.let { company ->
                dataStore.saveStringValue(COMPANY_NAME_KEY, company)
            }
            dataStore.saveBooleanValue(IS_ADMIN_KEY, model.isAdmin)
            dataStore.saveBooleanValue(IS_PRODUCER_KEY, model.isProducer)

            model.name?.let { name ->
                dataStore.saveStringValue(NAME_KEY, name)
            }

            model.surname?.let {
                dataStore.saveStringValue(SURNAME_KEY, it)
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
            .set(user.toMapWithoutId())
            .await()
    }

    override suspend fun upsertDeviceSnapshot(userId: String, snapshot: DeviceSnapshotModel) {
        val deviceId = snapshot.deviceId.orEmpty()
        if (deviceId.isBlank()) {
            Log.w(TAG, "DEVICE_SNAPSHOT_SKIP - deviceId vacío para userId=$userId")
            return
        }
        val deviceRef = collection.document(userId)
            .collection(DEVICES)
            .document(deviceId)

        val existingSnapshot = deviceRef.get().await()
        val data = snapshot.toMap().toMutableMap().apply {
            this["lastSeenAt"] = FieldValue.serverTimestamp()
            if (!existingSnapshot.exists()) {
                this["firstSeenAt"] = FieldValue.serverTimestamp()
            }
        }
        deviceRef.set(data, SetOptions.merge()).await()
        collection.document(userId)
            .set(mapOf(LAST_DEVICE_ID to deviceId), SetOptions.merge())
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
            //val userMap = user.toMapWithoutId()
            collection
                .add(user.toMapWithoutId())
                .await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
