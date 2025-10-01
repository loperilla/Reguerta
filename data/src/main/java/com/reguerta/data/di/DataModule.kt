package com.reguerta.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.auth.AuthServiceImpl
import com.reguerta.data.firebase.firestore.CONTAINERS
import com.reguerta.data.firebase.firestore.CONTAINERS_COLLECTION
import com.reguerta.data.firebase.firestore.FirestoreManager
import com.reguerta.data.firebase.firestore.MEASURES
import com.reguerta.data.firebase.firestore.MEASURES_COLLECTION
import com.reguerta.data.firebase.firestore.ORDERS
import com.reguerta.data.firebase.firestore.ORDERS_COLLECTION
import com.reguerta.data.firebase.firestore.ORDERS_LINES_COLLECTION
import com.reguerta.data.firebase.firestore.ORDER_LINES
import com.reguerta.data.firebase.firestore.PRODUCTS
import com.reguerta.data.firebase.firestore.PRODUCTS_COLLECTION
import com.reguerta.data.firebase.firestore.PRODUCT_IMAGE_STORAGE_PATH
import com.reguerta.data.firebase.firestore.USERS
import com.reguerta.data.firebase.firestore.USERS_COLLECTION
import com.reguerta.data.firebase.firestore.containers.ContainersService
import com.reguerta.data.firebase.firestore.containers.ContainersServiceImpl
import com.reguerta.data.firebase.firestore.measures.MeasuresService
import com.reguerta.data.firebase.firestore.measures.MeasuresServiceImpl
import com.reguerta.data.firebase.firestore.orders.OrdersServiceImpl
import com.reguerta.data.firebase.firestore.orders.OrdersService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesService
import com.reguerta.data.firebase.firestore.orderlines.OrderLinesServiceImpl
import com.reguerta.data.firebase.firestore.products.ProductsService
import com.reguerta.data.firebase.firestore.products.ProductsServiceImpl
import com.reguerta.data.firebase.firestore.users.UserCollectionImpl
import com.reguerta.data.firebase.firestore.users.UsersCollectionService
import com.reguerta.localdata.database.dao.MeasureDao
import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.time.WeekTime
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/*****
 * Project: Reguerta
 * From: com.reguerta.data.di
 * Created By Manuel Lopera on 24/1/24 at 11:33
 * All rights reserved 2024
 */

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthService(
        firebaseAuth: FirebaseAuth,
        dataStore: ReguertaDataStore,
        userCollection: UsersCollectionService,
        weekTime: WeekTime
    ): AuthService =
        AuthServiceImpl(firebaseAuth, userCollection, dataStore, weekTime)

    @Singleton
    @Provides
    fun provideFirestoreManager(): FirestoreManager = FirestoreManager

    @Named(USERS_COLLECTION)
    @Singleton
    @Provides
    fun provideUsersCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(USERS)

    @Singleton
    @Provides
    fun provideUserCollectionService(
        @Named(USERS_COLLECTION) collection: CollectionReference,
        dataStore: ReguertaDataStore
    ): UsersCollectionService = UserCollectionImpl(collection, dataStore)

    @Named(PRODUCTS_COLLECTION)
    @Singleton
    @Provides
    fun provideProductsCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(PRODUCTS)

    @Named(PRODUCT_IMAGE_STORAGE_PATH)
    @Singleton
    @Provides
    fun provideProductStoragePreference(): StorageReference =
        FirebaseStorage.getInstance().reference.child(PRODUCT_IMAGE_STORAGE_PATH)

    @Singleton
    @Provides
    fun provideProductsCollectionService(
        @Named(PRODUCTS_COLLECTION) collection: CollectionReference,
        dataStore: ReguertaDataStore,
        @Named(PRODUCT_IMAGE_STORAGE_PATH) storageReference: StorageReference
    ): ProductsService = ProductsServiceImpl(collection, dataStore, storageReference)

    @Named(CONTAINERS_COLLECTION)
    @Singleton
    @Provides
    fun provideContainerCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(CONTAINERS)

    @Singleton
    @Provides
    fun provideContainerCollectionService(
        @Named(CONTAINERS_COLLECTION) collection: CollectionReference
    ): ContainersService = ContainersServiceImpl(collection)

    @Named(ORDERS_COLLECTION)
    @Singleton
    @Provides
    fun provideOrderCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(ORDERS)

    @Singleton
    @Provides
    fun provideOrderCollectionService(
        @Named(ORDERS_COLLECTION) collection: CollectionReference,
        dataStore: ReguertaDataStore,
        weekTime: WeekTime
    ): OrdersService = OrdersServiceImpl(collection, dataStore, weekTime)

    @Named(ORDERS_LINES_COLLECTION)
    @Singleton
    @Provides
    fun provideOrderLinesCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(ORDER_LINES)

    @Singleton
    @Provides
    fun provideOrderLinesService(
        @Named(ORDERS_LINES_COLLECTION) collection: CollectionReference,
        orderLineDao: OrderLineDao,
        dataStore: ReguertaDataStore,
        weekTime: WeekTime
    ): OrderLinesService = OrderLinesServiceImpl(collection, orderLineDao, weekTime, dataStore)

    @Named(MEASURES_COLLECTION)
    @Singleton
    @Provides
    fun provideMeasuresCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.getCollection(MEASURES)

    @Singleton
    @Provides
    fun provideMeasuresCollectionService(
        @Named(MEASURES_COLLECTION) collection: CollectionReference,
        measureDao: MeasureDao
    ): MeasuresService = MeasuresServiceImpl(collection, measureDao)
}
