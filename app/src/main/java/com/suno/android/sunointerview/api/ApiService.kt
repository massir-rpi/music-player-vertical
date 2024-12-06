package com.suno.android.sunointerview.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("songs")
    suspend fun getSongs(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 10
    ) : Response<ApiResponse>
}
