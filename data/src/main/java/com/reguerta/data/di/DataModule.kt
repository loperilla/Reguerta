package com.reguerta.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.reguerta.data.firebase.auth.AuthService
import com.reguerta.data.firebase.auth.AuthServiceImpl
import com.reguerta.data.firebase.firestore.CONTAINER
import com.reguerta.data.firebase.firestore.CONTAINERS_COLLECTION
import com.reguerta.data.firebase.firestore.MEASURES
import com.reguerta.data.firebase.firestore.MEASURES_COLLECTION
import com.reguerta.data.firebase.firestore.ORDER
import com.reguerta.data.firebase.firestore.ORDERS_COLLECTION
import com.reguerta.data.firebase.firestore.ORDERS_LINES_COLLECTION
import com.reguerta.data.firebase.firestore.ORDER_LINES
import com.reguerta.data.firebase.firestore.PRODUCTS
import com.reguerta.data.firebase.firestore.PRODUCTS_COLLECTION
import com.reguerta.data.firebase.firestore.PRODUCT_IMAGE_STORAGE_PATH
import com.reguerta.data.firebase.firestore.USERS
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
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Named(USERS_COLLECTION)
    @Singleton
    @Provides
    fun provideUsersCollection(firestore: FirebaseFirestore) = firestore.collection(USERS)

    @Singleton
    @Provides
    fun provideUserCollectionService(
        @Named(USERS_COLLECTION) collection: CollectionReference,
        dataStore: ReguertaDataStore
    ): UsersCollectionService = UserCollectionImpl(collection, dataStore)

    @Named(PRODUCTS_COLLECTION)
    @Singleton
    @Provides
    fun provideProductsCollection(firestore: FirebaseFirestore) = firestore.collection(PRODUCTS)

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
    fun provideContainerCollection(firestore: FirebaseFirestore) = firestore.collection(CONTAINER)

    @Singleton
    @Provides
    fun provideContainerCollectionService(
        @Named(CONTAINERS_COLLECTION) collection: CollectionReference
    ): ContainerService = ContainerServiceImpl(collection)

    @Named(ORDERS_COLLECTION)
    @Singleton
    @Provides
    fun provideOrderCollection(firestore: FirebaseFirestore) = firestore.collection(ORDER)

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
    fun provideOrderLinesCollection(firestore: FirebaseFirestore) = firestore.collection(ORDER_LINES)

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
    fun provideMeasuresCollection(firestore: FirebaseFirestore) = firestore.collection(MEASURES)

    @Singleton
    @Provides
    fun provideMeasuresCollectionService(
        @Named(MEASURES_COLLECTION) collection: CollectionReference,
        measureDao: MeasureDao
    ): MeasureService = MeasureServiceImpl(collection, measureDao)
}
