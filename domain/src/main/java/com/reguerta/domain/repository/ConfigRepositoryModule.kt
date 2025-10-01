package com.reguerta.domain.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ConfigRepositoryModule {

    @Binds
    abstract fun bindConfigRepository(
        impl: ConfigRepositoryImpl
    ): ConfigRepository
}