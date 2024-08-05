package com.example.compose_player.ui.view.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_player.data.model.MusicModelItem
import com.example.compose_player.data.repository.MusicRepository
import com.example.compose_player.utils.network.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val repository: MusicRepository) : ViewModel() {
    val musicData: MutableState<DataState<List<MusicModelItem>>> = mutableStateOf(DataState.Loading)

    fun getMusicData() {
        viewModelScope.launch {
            repository.loadMusic().onEach {
                musicData.value = it
            }.launchIn(viewModelScope)
        }
    }
}