package com.reguerta.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.auth.AuthServiceImpl
import com.reguerta.data.firebase.firestore.CONTAINERS_COLLECTION
import com.reguerta.data.firebase.firestore.FirestoreManager
import com.reguerta.data.firebase.firestore.MEASURES_COLLECTION
import com.reguerta.data.firebase.firestore.ORDERS_COLLECTION
import com.reguerta.data.firebase.firestore.ORDERS_LINES_COLLECTION
import com.reguerta.data.firebase.firestore.PRODUCTS_COLLECTION
import com.reguerta.data.firebase.firestore.PRODUCT_IMAGE_STORAGE_PATH
import com.reguerta.data.firebase.firestore.USERS_COLLECTION
import com.reguerta.data.firebase.firestore.container.ContainerService
import com.reguerta.data.firebase.firestore.container.ContainerServiceImpl
import com.reguerta.data.firebase.firestore.measures.MeasureService
import com.reguerta.data.firebase.firestore.measures.MeasureServiceImpl
import com.reguerta.data.firebase.firestore.order.OrderServiceImpl
import com.reguerta.data.firebase.firestore.order.OrderServices
import com.reguerta.data.firebase.firestore.orderlines.OrderLineService
import com.reguerta.data.firebase.firestore.orderlines.OrderLineServiceImpl
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
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

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
        firestoreManager.usersCollection

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
        firestoreManager.productsCollection

    @Named(PRODUCT_IMAGE_STORAGE_PATH)
    @Singleton
    @Provides
    fun provideProductStoragePreference(): StorageReference = Firebase
        .storage
        .reference
        .child(PRODUCT_IMAGE_STORAGE_PATH)

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
        firestoreManager.containersCollection

    @Singleton
    @Provides
    fun provideContainerCollectionService(
        @Named(CONTAINERS_COLLECTION) collection: CollectionReference
    ): ContainerService = ContainerServiceImpl(collection)

    @Named(ORDERS_COLLECTION)
    @Singleton
    @Provides
    fun provideOrderCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.ordersCollection

    @Singleton
    @Provides
    fun provideOrderCollectionService(
        @Named(ORDERS_COLLECTION) collection: CollectionReference,
        dataStore: ReguertaDataStore,
        weekTime: WeekTime
    ): OrderServices = OrderServiceImpl(collection, dataStore, weekTime)

    @Named(ORDERS_LINES_COLLECTION)
    @Singleton
    @Provides
    fun provideOrderLinesCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.linesCollection

    @Singleton
    @Provides
    fun provideOrderLinesService(
        @Named(ORDERS_LINES_COLLECTION) collection: CollectionReference,
        orderLineDao: OrderLineDao,
        dataStore: ReguertaDataStore,
        weekTime: WeekTime
    ): OrderLineService = OrderLineServiceImpl(collection, orderLineDao, weekTime, dataStore)

    @Named(MEASURES_COLLECTION)
    @Singleton
    @Provides
    fun provideMeasuresCollection(firestoreManager: FirestoreManager): CollectionReference =
        firestoreManager.measuresCollection

    @Singleton
    @Provides
    fun provideMeasuresCollectionService(
        @Named(MEASURES_COLLECTION) collection: CollectionReference,
        measureDao: MeasureDao
    ): MeasureService = MeasureServiceImpl(collection, measureDao)
}
