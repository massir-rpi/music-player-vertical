package com.suno.android.sunointerview.music

import com.suno.android.sunointerview.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getSongs(page: Int, pageSize: Int = 10) = apiService.getSongs(page, pageSize)
}
