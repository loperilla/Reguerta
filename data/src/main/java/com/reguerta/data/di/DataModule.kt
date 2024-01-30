package com.reguerta.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.reguerta.data.firebase.AuthService
import com.reguerta.data.firebase.AuthServiceImpl
import com.reguerta.localdata.datastore.ReguertaDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideAuthService(firebaseAuth: FirebaseAuth, dataStore: ReguertaDataStore): AuthService =
        AuthServiceImpl(firebaseAuth, dataStore)
}
