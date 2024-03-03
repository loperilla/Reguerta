package com.reguerta.data.firebase.firestore.products

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.reguerta.data.firebase.firestore.USER_ID
import com.reguerta.localdata.datastore.COMPANY_NAME_KEY
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.UID_KEY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/*****
 * Project: Reguerta
 * From: com.reguerta.data.firebase.firestore.products
 * Created By Manuel Lopera on 24/2/24 at 15:26
 * All rights reserved 2024
 */
class ProductsServiceImpl @Inject constructor(
    private val collection: CollectionReference,
    private val dataStore: ReguertaDataStore,
    private val storage: StorageReference
) : ProductsService {
    override suspend fun getProducts(): Flow<Result<List<ProductModel>>> = callbackFlow {
        val subscription = collection
            .whereEqualTo(
                USER_ID,
                dataStore.getStringByKey(UID_KEY)
            )
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    trySend(Result.failure(error))
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { query ->
                    val productList = mutableListOf<ProductModel>()
                    query.documents.forEach { document ->
                        val product = document.toObject(ProductModel::class.java)
                        product?.let { model ->
                            model.id = document.id
                            productList.add(model)
                        }
                    }
                    trySend(Result.success(productList))
                }
            }
        awaitClose {
            subscription.remove()
        }
    }

    override suspend fun deleteProduct(id: String) {
        collection
            .document(id)
            .delete()
            .await()
    }

    override suspend fun addProduct(product: ProductDTOModel, byteArray: ByteArray?): Result<Unit> {
        return try {
            var productToCreate = product.copy(
                userId = dataStore.getStringByKey(UID_KEY),
                companyName = dataStore.getStringByKey(COMPANY_NAME_KEY)
            )
            if (byteArray != null) {
                val imageRef = storage.child(productToCreate.buildImageRef())
                imageRef.putBytes(byteArray).await()
                val url = imageRef.downloadUrl.await()
                productToCreate = productToCreate.copy(
                    urlImage = url.toString()
                )
            }
            collection
                .add(productToCreate)
                .await()
            Result.success(Unit)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}