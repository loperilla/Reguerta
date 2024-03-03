package com.reguerta.localdata.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.datastore
 * Created By Manuel Lopera on 30/1/24 at 10:47
 * All rights reserved 2024
 */
val Context.userPreferences by preferencesDataStore("UserDatastore")

class ReguertaDataStoreImpl @Inject constructor(private val context: Context) : ReguertaDataStore {
    override suspend fun saveStringValue(key: Preferences.Key<String>, value: String): Unit =
        withContext(Dispatchers.IO) {
        context.userPreferences.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun saveBooleanValue(key: Preferences.Key<Boolean>, value: Boolean) {
        context.userPreferences.edit {
            it[key] = value
        }
    }

    override suspend fun getStringByKey(key: Preferences.Key<String>): String = withContext(Dispatchers.IO) {
        context.userPreferences.data.first()[key] ?: ""
    }

    override suspend fun clearUserDataStore(): Unit = withContext(Dispatchers.IO) {
        context.userPreferences.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun getBooleanByKey(key: Preferences.Key<Boolean>): Boolean = withContext(Dispatchers.IO) {
        context.userPreferences.data.first()[key] ?: false
    }
}