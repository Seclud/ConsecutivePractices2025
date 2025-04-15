package com.example.consecutivepractice.api

import com.example.consecutivepractice.models.GameDetailsResponse
import com.example.consecutivepractice.models.GamesListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GamesApi {
    @GET("games")
    suspend fun getGames(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<GamesListResponse>

    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") id: Int
    ): Response<GameDetailsResponse>
}
