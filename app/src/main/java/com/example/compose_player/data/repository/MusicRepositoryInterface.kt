package com.example.compose_player.data.repository

import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.utils.network.DataState
import kotlinx.coroutines.flow.Flow

interface MusicRepositoryInterface {

    suspend  fun loadMusic(): Flow<DataState<List<MusicModelItem>>>
}