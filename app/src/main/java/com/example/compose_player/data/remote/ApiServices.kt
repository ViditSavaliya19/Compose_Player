package com.example.compose_player.data.remote

import com.example.compose_player.data.model.MusicModelItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiServices {

    @GET("JSON/music.json")
    fun getMusic(): Call<List<MusicModelItem>>
}