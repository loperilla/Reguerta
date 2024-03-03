package com.reguerta.localdata.datastore

import androidx.datastore.preferences.core.Preferences

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
}