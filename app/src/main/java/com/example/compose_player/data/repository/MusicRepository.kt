package com.example.compose_player.data.repository

import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.data.remote.ApiServices
import com.example.compose_player.utils.network.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.await
import javax.inject.Inject

class MusicRepository @Inject constructor(private val apiServices: ApiServices) :
    MusicRepositoryInterface {
    override suspend fun loadMusic(): Flow<DataState<List<MusicModelItem>>> = flow {
        emit(DataState.Loading)
        try {
            val response = apiServices.getMusic().await()
            emit(DataState.Success(response))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}