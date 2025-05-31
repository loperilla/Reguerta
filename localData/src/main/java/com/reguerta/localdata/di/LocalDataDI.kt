package com.reguerta.localdata.di

import android.content.Context
import androidx.room.Room
import com.reguerta.localdata.database.ReguertaDatabase
import com.reguerta.localdata.database.dao.MeasureDao
import com.reguerta.localdata.database.dao.OrderLineDao
import com.reguerta.localdata.datastore.ReguertaDataStore
import com.reguerta.localdata.datastore.ReguertaDataStoreImpl
import com.reguerta.localdata.time.WeekTime
import com.reguerta.localdata.time.WeekTimeImpl
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
    fun provideReguertaDataStore(@ApplicationContext context: Context): ReguertaDataStore = ReguertaDataStoreImpl(context)

    @Singleton
    @Provides
    fun provideWeekTime(): WeekTime = WeekTimeImpl()

    @Singleton
    @Provides
    fun provideDatabase(
        context: Context
    ): ReguertaDatabase = Room
        .databaseBuilder(
            context,
            ReguertaDatabase::class.java,
            ReguertaDatabase::class.java.simpleName
        ).build()

    @Singleton
    @Provides
    fun provideOrderLineDao(
        reguertaDatabase: ReguertaDatabase
    ): OrderLineDao = reguertaDatabase.orderLineDao()

    @Singleton
    @Provides
    fun provideMeasureDao(
        reguertaDatabase: ReguertaDatabase
    ): MeasureDao = reguertaDatabase.measureDao()
}