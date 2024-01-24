package com.reguerta.data.datastore

/*****
 * Project: Reguerta
 * From: com.reguerta.data.datastore
 * Created By Manuel Lopera on 24/1/24 at 11:46
 * All rights reserved 2024
 */
interface ReguertaDataStore {
    suspend fun saveUID(uid: String)
    suspend fun getUID(): String
    suspend fun clearUID()
}