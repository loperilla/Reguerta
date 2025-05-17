package com.reguerta.localdata.datastore

import androidx.datastore.preferences.core.longPreferencesKey

fun syncTimestampKey(tableName: String) = longPreferencesKey("sync_ts_$tableName")