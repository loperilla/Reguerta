package com.reguerta.localdata.di

import android.content.Context
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.ReguertaDataStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*****
 * Project: Reguerta
 * From: com.reguerta.localdata.di
 * Created By Manuel Lopera on 30/1/24 at 10:48
 * All rights reserved 2024
 */

@Module
@InstallIn(SingletonComponent::class)
object LocalDataDI {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Singleton
    @Provides
    fun provideReguertaDataStore(context: Context): ReguertaDataStore = ReguertaDataStoreImpl(context)
}