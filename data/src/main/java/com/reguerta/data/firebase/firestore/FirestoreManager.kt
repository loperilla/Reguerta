package com.reguerta.data.firebase.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.reguerta.data.BuildConfig

enum class FirestoreEnvironment(val path: String) {
    DEVELOP("develop"),
    PRODUCTION("production")
}

object FirestoreManager {

    private var currentEnvironment: FirestoreEnvironment = if (BuildConfig.DEBUG) {
        FirestoreEnvironment.DEVELOP
    } else {
        FirestoreEnvironment.PRODUCTION
    }

    // Cambia el entorno dinámicamente, si es necesario
    fun setEnvironment(environment: FirestoreEnvironment) {
        currentEnvironment = environment
    }
    private fun getFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
    private val root: DocumentReference
        get() = getFirestoreInstance().collection(currentEnvironment.path).document("collections")

    val usersCollection: CollectionReference
        get() = root.collection(USERS)

    val containersCollection: CollectionReference
        get() = root.collection(CONTAINERS)

    val measuresCollection: CollectionReference
        get() = root.collection(MEASURES)

    val productsCollection: CollectionReference
        get() = root.collection(PRODUCTS)

    val ordersCollection: CollectionReference
        get() = root.collection(ORDERS)

    val linesCollection: CollectionReference
        get() = root.collection(ORDER_LINES)

    val newsCollection: CollectionReference
        get() = root.collection(NEWS)
}
