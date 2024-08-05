package com.example.compose_player.di

import com.example.compose_player.data.remote.ApiServices
import com.example.compose_player.data.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}