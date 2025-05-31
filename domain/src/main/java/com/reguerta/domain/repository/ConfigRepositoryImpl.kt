package com.reguerta.domain.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.reguerta.data.firebase.firestore.FirestoreManager
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import timber.log.Timber

class ConfigRepositoryImpl @Inject constructor() : ConfigRepository {
    override suspend fun getGlobalConfig(): ConfigModel = suspendCoroutine { cont ->
        FirestoreManager.getCollection("config")
            .document("global")
            .get()
            .addOnSuccessListener { document ->
                val data = document.data.orEmpty()

                val timestampsMap = (data["lastTimestamps"] as? Map<*, *>)
                    ?.mapNotNull { (k, v) ->
                        if (k is String && v is Timestamp) k to v else null
                    }?.toMap() ?: emptyMap()

                val rawVersions = (data["versions"] as? Map<*, *>).orEmpty()

                val versions = rawVersions.mapNotNull { (platformKey, versionData) ->
                    if (platformKey is String && versionData is Map<*, *>) {
                        val min = versionData["min"] as? String ?: ""
                        val current = versionData["current"] as? String ?: ""
                        val forceUpdate = when (val value = versionData["forceUpdate"]) {
                            is Boolean -> value
                            is String -> value.toBooleanStrictOrNull() ?: false
                            else -> false
                        }
                        val storeUrl = versionData["storeUrl"] as? String ?: ""
                        Timber.d("SYNC_VersionInfo for $platformKey: min=$min, current=$current, forceUpdate=$forceUpdate, storeUrl=$storeUrl")
                        platformKey to VersionInfo(min, current, forceUpdate, storeUrl)
                    } else null
                }.toMap()

                val cacheExpiration = (data["cacheExpirationMinutes"] as? Long)?.toInt() ?: 30

                val otherConfig: Map<String, Any?> = (data["otherConfig"] as? Map<*, *>)?.mapNotNull { (k, v) ->
                    if (k is String) k to v else null
                }?.toMap() ?: emptyMap<String, Any>()

                cont.resume(
                    ConfigModel(
                        lastTimestamps = timestampsMap,
                        versions = versions,
                        cacheExpirationMinutes = cacheExpiration,
                        otherConfig = otherConfig
                    )
                )
            }
            .addOnFailureListener { cont.resumeWithException(it) }
    }
    override suspend fun updateTableTimestamp(table: String) = suspendCoroutine<Unit> { cont ->
        FirestoreManager.getCollection("config")
            .document("global")
            .update("lastTimestamps.$table", FieldValue.serverTimestamp())
            .addOnSuccessListener { cont.resume(Unit) }
            .addOnFailureListener { cont.resumeWithException(it) }
    }
}