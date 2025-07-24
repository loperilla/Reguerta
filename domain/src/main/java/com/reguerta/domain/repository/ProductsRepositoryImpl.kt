package com.reguerta.domain.repository

import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.reguerta.domain.model.CommonProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ProductsRepositoryImpl @Inject constructor(
    // Inyecta aquí tus DAOs, servicios remotos, etc. Por ejemplo:
    // private val firestoreService: FirestoreService,
    // private val localDao: LocalProductDao
) : ProductsRepository {

    override suspend fun preloadProducts() {
        // Pendiente de implementar en la refactorización general.
        // Actualmente la precarga de productos se gestiona directamente desde el UseCase,
        // que inyecta y usa ProductsService. Esta función se implementará correctamente tras la migración a Clean Architecture.
    }

    override fun getAvailableProducts(forceFromServer: Boolean): Flow<List<CommonProduct>> = callbackFlow {
        val source = if (forceFromServer) Source.SERVER else Source.DEFAULT
        FirebaseFirestore.getInstance().collection("products")
            .get(source)
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull {
                    it.toObject(CommonProduct::class.java)
                }
                trySend(products)
            }
            .addOnFailureListener {
                close(it)
            }
        awaitClose { /* No-op: one-shot fetch */ }
    }
}