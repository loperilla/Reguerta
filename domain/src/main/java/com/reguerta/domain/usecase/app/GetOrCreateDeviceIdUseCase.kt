package com.reguerta.domain.usecase.app

import com.reguerta.localdata.datastore.DEVICE_ID_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import java.util.UUID
import javax.inject.Inject

class GetOrCreateDeviceIdUseCase @Inject constructor(
    private val dataStore: ReguertaDataStore
) {
    suspend operator fun invoke(): String {
        val existing = dataStore.getStringByKey(DEVICE_ID_KEY)
        if (existing.isNotBlank()) {
            return existing
        }
        val newId = UUID.randomUUID().toString()
        dataStore.saveStringValue(DEVICE_ID_KEY, newId)
        return newId
    }
}
