package com.bysoftware.aaeksen.presentation.video

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.VideoComment
import com.bysoftware.aaeksen.data.firebase.model.VideoContent
import com.bysoftware.aaeksen.data.firebase.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoPlayerUiState>(VideoPlayerUiState.Loading)
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    private val _currentVideo = MutableStateFlow<VideoContent?>(null)
    val currentVideo: StateFlow<VideoContent?> = _currentVideo.asStateFlow()

    private val _comments = MutableStateFlow<List<VideoComment>>(emptyList())
    val comments: StateFlow<List<VideoComment>> = _comments.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _uiState.value = VideoPlayerUiState.Loading
            try {
                val result = videoRepository.getVideoById(videoId)
                result.onSuccess { video ->
                    if (video != null) {
                        _currentVideo.value = video
                        _uiState.value = VideoPlayerUiState.Success
                        Log.d("VideoPlayerViewModel", "✅ Video yüklendi: ${video.title}")
                    } else {
                        _uiState.value = VideoPlayerUiState.Error("Video bulunamadı")
                    }
                }.onFailure { error ->
                    _uiState.value = VideoPlayerUiState.Error(error.message ?: "Video yüklenemedi")
                    Log.e("VideoPlayerViewModel", "❌ Video yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                _uiState.value = VideoPlayerUiState.Error(e.message ?: "Beklenmeyen hata")
                Log.e("VideoPlayerViewModel", "❌ Beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun loadComments(videoId: String) {
        viewModelScope.launch {
            try {
                val result = videoRepository.getVideoComments(videoId)
                result.onSuccess { commentList ->
                    _comments.value = commentList
                    Log.d("VideoPlayerViewModel", "✅ ${commentList.size} yorum yüklendi")
                }.onFailure { error ->
                    Log.e("VideoPlayerViewModel", "❌ Yorumlar yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoPlayerViewModel", "❌ Yorumlar yüklenirken beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun incrementViewCount(videoId: String) {
        viewModelScope.launch {
            try {
                videoRepository.incrementViewCount(videoId)
                // Mevcut video'nun view count'unu güncelle
                _currentVideo.value?.let { video ->
                    _currentVideo.value = video.copy(viewCount = video.viewCount + 1)
                }
            } catch (e: Exception) {
                Log.e("VideoPlayerViewModel", "❌ View count artırılırken hata: ${e.message}")
            }
        }
    }

    fun likeVideo(videoId: String) {
        viewModelScope.launch {
            try {
                videoRepository.incrementLikeCount(videoId)
                // Mevcut video'nun like count'unu güncelle
                _currentVideo.value?.let { video ->
                    _currentVideo.value = video.copy(likeCount = video.likeCount + 1)
                }
                Log.d("VideoPlayerViewModel", "✅ Video beğenildi")
            } catch (e: Exception) {
                Log.e("VideoPlayerViewModel", "❌ Video beğenilirken hata: ${e.message}")
            }
        }
    }

    fun shareVideo(video: VideoContent) {
        try {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, 
                    "Bu videoyu izle: ${video.title}\n${video.videoUrl}")
                putExtra(Intent.EXTRA_SUBJECT, video.title)
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "Videoyu Paylaş")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)

            // Share count artır
            viewModelScope.launch {
                videoRepository.incrementShareCount(video.id)
                _currentVideo.value = video.copy(shareCount = video.shareCount + 1)
            }
            
            Log.d("VideoPlayerViewModel", "✅ Video paylaşıldı: ${video.title}")
        } catch (e: Exception) {
            Log.e("VideoPlayerViewModel", "❌ Video paylaşılırken hata: ${e.message}")
        }
    }

    fun downloadVideo(video: VideoContent) {
        // TODO: Video indirme özelliği
        // Bu özellik için DownloadManager kullanılabilir
        Log.d("VideoPlayerViewModel", "📥 Video indirme özelliği henüz implement edilmedi: ${video.title}")
    }

    fun addComment(videoId: String, content: String) {
        viewModelScope.launch {
            try {
                // Gerçek kullanıcı bilgileri yerine mock data kullanıyoruz
                val result = videoRepository.addComment(
                    videoId = videoId,
                    userId = "sample_user_123",
                    username = "Kamelia Ahmed",
                    userPhotoUrl = "https://images.unsplash.com/photo-1494790108755-2616b332c913?w=150&h=150&fit=crop&crop=face",
                    content = content
                )
                
                result.onSuccess { commentId ->
                    // Yorumları yeniden yükle
                    loadComments(videoId)
                    Log.d("VideoPlayerViewModel", "✅ Yorum eklendi: $commentId")
                }.onFailure { error ->
                    Log.e("VideoPlayerViewModel", "❌ Yorum eklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoPlayerViewModel", "❌ Yorum eklenirken beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun refresh(videoId: String) {
        loadVideo(videoId)
        loadComments(videoId)
    }
}

sealed class VideoPlayerUiState {
    object Loading : VideoPlayerUiState()
    object Success : VideoPlayerUiState()
    data class Error(val message: String) : VideoPlayerUiState()
}
