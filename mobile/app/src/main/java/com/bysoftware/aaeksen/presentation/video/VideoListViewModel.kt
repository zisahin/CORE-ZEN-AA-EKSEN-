package com.bysoftware.aaeksen.presentation.video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.VideoContent
import com.bysoftware.aaeksen.data.firebase.model.VideoPlaylist
import com.bysoftware.aaeksen.data.firebase.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoListUiState>(VideoListUiState.Loading)
    val uiState: StateFlow<VideoListUiState> = _uiState.asStateFlow()

    private val _videos = MutableStateFlow<List<VideoContent>>(emptyList())
    val videos: StateFlow<List<VideoContent>> = _videos.asStateFlow()

    private val _featuredVideos = MutableStateFlow<List<VideoContent>>(emptyList())
    val featuredVideos: StateFlow<List<VideoContent>> = _featuredVideos.asStateFlow()

    private val _playlists = MutableStateFlow<List<VideoPlaylist>>(emptyList())
    val playlists: StateFlow<List<VideoPlaylist>> = _playlists.asStateFlow()

    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadContent() {
        viewModelScope.launch {
            _uiState.value = VideoListUiState.Loading
            try {
                Log.d("VideoListViewModel", "🔄 İçerik yükleniyor...")
                
                // Sırayla içerikleri yükle
                val videosResult = videoRepository.getVideos(limit = 50)
                Log.d("VideoListViewModel", "📹 Videos result: ${videosResult.isSuccess}")
                
                val featuredResult = videoRepository.getFeaturedVideos(limit = 10)
                Log.d("VideoListViewModel", "⭐ Featured result: ${featuredResult.isSuccess}")
                
                val playlistsResult = videoRepository.getPlaylists(limit = 20)
                Log.d("VideoListViewModel", "📋 Playlists result: ${playlistsResult.isSuccess}")

                // Sonuçları işle
                videosResult.onSuccess { videoList ->
                    _videos.value = videoList
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Videolar yüklenirken hata: ${error.message}")
                }

                featuredResult.onSuccess { featuredList ->
                    _featuredVideos.value = featuredList
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Öne çıkan videolar yüklenirken hata: ${error.message}")
                }

                playlistsResult.onSuccess { playlistList ->
                    _playlists.value = playlistList
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Oynatma listeleri yüklenirken hata: ${error.message}")
                }

                _uiState.value = VideoListUiState.Success
                Log.d("VideoListViewModel", "✅ İçerik yüklendi - Videolar: ${_videos.value.size}, Öne çıkan: ${_featuredVideos.value.size}, Listeler: ${_playlists.value.size}")

            } catch (e: Exception) {
                _uiState.value = VideoListUiState.Error(e.message ?: "İçerik yüklenemedi")
                Log.e("VideoListViewModel", "❌ İçerik yüklenirken beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        
        _selectedCategory.value = category
        loadVideosByCategory(category)
    }

    private fun loadVideosByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = if (category.isEmpty()) {
                    videoRepository.getVideos(limit = 50)
                } else {
                    videoRepository.getVideos(category = category, limit = 50)
                }

                result.onSuccess { videoList ->
                    _videos.value = videoList
                    Log.d("VideoListViewModel", "✅ Kategori videoları yüklendi ($category): ${videoList.size}")
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Kategori videoları yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "❌ Kategori videoları yüklenirken beklenmeyen hata: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchVideos(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = videoRepository.searchVideos(query, limit = 50)
                result.onSuccess { videoList ->
                    _videos.value = videoList
                    Log.d("VideoListViewModel", "✅ Arama sonuçları yüklendi ($query): ${videoList.size}")
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Arama yapılırken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "❌ Arama yapılırken beklenmeyen hata: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreVideos() {
        if (_isLoading.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lastVideoId = _videos.value.lastOrNull()?.id
                val category = _selectedCategory.value.takeIf { it.isNotEmpty() }
                
                val result = videoRepository.getVideos(
                    category = category,
                    limit = 20,
                    lastVideoId = lastVideoId
                )

                result.onSuccess { newVideos ->
                    if (newVideos.isNotEmpty()) {
                        _videos.value = _videos.value + newVideos
                        Log.d("VideoListViewModel", "✅ Daha fazla video yüklendi: ${newVideos.size}")
                    }
                }.onFailure { error ->
                    Log.e("VideoListViewModel", "❌ Daha fazla video yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "❌ Daha fazla video yüklenirken beklenmeyen hata: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        _selectedCategory.value = ""
        loadContent()
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            try {
                videoRepository.incrementLikeCount(videoId)
                
                // Local state'i güncelle
                _videos.value = _videos.value.map { video ->
                    if (video.id == videoId) {
                        video.copy(likeCount = video.likeCount + 1)
                    } else {
                        video
                    }
                }
                
                _featuredVideos.value = _featuredVideos.value.map { video ->
                    if (video.id == videoId) {
                        video.copy(likeCount = video.likeCount + 1)
                    } else {
                        video
                    }
                }
                
                Log.d("VideoListViewModel", "✅ Video beğenildi: $videoId")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "❌ Video beğenilirken hata: ${e.message}")
            }
        }
    }

    fun shareVideo(videoId: String) {
        viewModelScope.launch {
            try {
                videoRepository.incrementShareCount(videoId)
                
                // Local state'i güncelle
                _videos.value = _videos.value.map { video ->
                    if (video.id == videoId) {
                        video.copy(shareCount = video.shareCount + 1)
                    } else {
                        video
                    }
                }
                
                Log.d("VideoListViewModel", "✅ Video paylaşıldı: $videoId")
            } catch (e: Exception) {
                Log.e("VideoListViewModel", "❌ Video paylaşılırken hata: ${e.message}")
            }
        }
    }

}

sealed class VideoListUiState {
    object Loading : VideoListUiState()
    object Success : VideoListUiState()
    data class Error(val message: String) : VideoListUiState()
}
