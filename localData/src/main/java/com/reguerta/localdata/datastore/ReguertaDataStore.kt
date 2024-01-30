package com.reguerta.localdata.datastore

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.datastore
 * Created By Manuel Lopera on 30/1/24 at 10:46
 * All rights reserved 2024
 */
interface ReguertaDataStore {
    suspend fun saveUID(uid: String)
    suspend fun getUID(): String
    suspend fun clearUID()
}