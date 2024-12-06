package com.suno.android.sunointerview.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suno.android.sunointerview.api.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
) : ViewModel() {
    private val _songsFlow = MutableStateFlow<ApiResponse?>(null)
    val songsFlow = _songsFlow.asStateFlow()

    private var page = 0

    fun loadNextPage() {
        page += 1
        viewModelScope.launch {
            _songsFlow.value = musicRepository.getSongs(page).body()
        }
    }
}
