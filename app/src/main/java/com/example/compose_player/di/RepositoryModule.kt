package com.example.compose_player.di

import android.app.Application
import android.content.Context
import com.example.compose_player.data.remote.ApiServices
import com.example.compose_player.data.repository.MusicRepository
import com.example.compose_player.data.services.MusicControllerImpl
import com.example.compose_player.domain.service.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(apiServices: ApiServices): MusicRepository {
        return MusicRepository(apiServices)
    }

    @Singleton
    @Provides
    fun provideApplicationContext(): ApplicationContext {
        return  ApplicationContext()
    }
    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)


}