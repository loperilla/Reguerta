package com.reguerta.data.firebase.firestore

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.perf.FirebasePerformance

enum class FirestoreEnvironment(val path: String) {
    DEVELOP("develop"),
    PRODUCTION("production")
}
object FirestoreManager {
    @Volatile var currentEnvironment: FirestoreEnvironment? = null

    fun configureFirestore(settings: FirebaseFirestoreSettings? = null) {
        try {
            val defaultSettings = settings ?: FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(20L * 1024 * 1024)    // 20 Mb de caché
                        .build()
                )
                .build()
            getFirestoreInstance().firestoreSettings = defaultSettings
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error configurando Firestore", e)
        }
    }

    fun setEnvironment(environment: FirestoreEnvironment) {
        synchronized(this) {
            currentEnvironment = environment
            Log.d("FirestoreManager", "Entorno configurado: ${environment.path}")
        }
    }

    private fun getFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    private val root: DocumentReference
        get() {
            val environment = currentEnvironment ?: throw IllegalStateException("El entorno debe configurarse antes de acceder a FirestoreManager.")
            return getFirestoreInstance().collection(environment.path).document("collections")
        }

    fun getCollection(collectionName: String): CollectionReference {
        return root.collection(collectionName)
    }
}