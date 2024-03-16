package com.reguerta.localdata.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.datastore
 * Created By Manuel Lopera on 30/1/24 at 10:46
 * All rights reserved 2024
 */


val UID_KEY = stringPreferencesKey("uid")
val NAME_KEY = stringPreferencesKey("name")
val SURNAME_KEY = stringPreferencesKey("surname")
val COMPANY_NAME_KEY = stringPreferencesKey("companyName")

val IS_ADMIN_KEY = booleanPreferencesKey("isAdmin")
val IS_PRODUCER_KEY = booleanPreferencesKey("isProducer")