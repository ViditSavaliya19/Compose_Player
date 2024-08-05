package com.example.compose_player.utils.network

import com.example.compose_player.data.model.Data
import java.lang.Exception

sealed class DataState<out R> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()
    data object Loading:DataState<Nothing>()
}