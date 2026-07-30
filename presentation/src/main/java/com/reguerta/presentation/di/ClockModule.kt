package com.reguerta.presentation.di

import com.reguerta.domain.time.ClockProvider
import com.reguerta.presentation.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    fun provideClockProvider(
        @Named("systemClock") systemClock: ClockProvider
    ): ClockProvider {
        val override: LocalDate? = BuildConfig.DEBUG_LOGIN_DATE
            .takeIf { it.isNotBlank() }
            ?.let(LocalDate::parse)

        return object : ClockProvider {
            override fun today(): LocalDate = override ?: systemClock.today()
            override fun now() = systemClock.now() // si tu interfaz lo tiene
        }
    }
}