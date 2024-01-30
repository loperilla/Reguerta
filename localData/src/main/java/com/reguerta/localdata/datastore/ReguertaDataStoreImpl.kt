package com.reguerta.localdata.datastore

import android.content.Context
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
    override suspend fun saveUID(uid: String): Unit = withContext(Dispatchers.IO) {
        context.userPreferences.edit { preferences ->
            preferences[UID_KEY] = uid
        }
    }


    override suspend fun getUID(): String = withContext(Dispatchers.IO) {
        context.userPreferences.data.first()[UID_KEY] ?: ""
    }

    override suspend fun clearUID(): Unit = withContext(Dispatchers.IO) {
        context.userPreferences.edit { preferences ->
            preferences.remove(UID_KEY)
        }
    }
}