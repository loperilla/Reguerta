package com.reguerta.localdata.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.datastore
 * Created By Manuel Lopera on 30/1/24 at 10:46
 * All rights reserved 2024
 */

interface ReguertaDataStore {
    suspend fun saveStringValue(key: Preferences.Key<String>, value: String)
    suspend fun saveBooleanValue(key: Preferences.Key<Boolean>, value: Boolean)
    suspend fun getStringByKey(key: Preferences.Key<String>): String
    suspend fun getBooleanByKey(key: Preferences.Key<Boolean>): Boolean
    suspend fun clearUserDataStore()
    suspend fun saveSyncTimestamp(tableKey: String, timestamp: Long)
    suspend fun getSyncTimestamp(tableKey: String): Long
    suspend fun saveAllSyncTimestamps(timestamps: Map<String, Long>)
    suspend fun getAllSyncTimestamps(): Map<String, Long>
    suspend fun getSyncTimestampsFor(tableKeys: List<String>): Map<String, Long>
}
