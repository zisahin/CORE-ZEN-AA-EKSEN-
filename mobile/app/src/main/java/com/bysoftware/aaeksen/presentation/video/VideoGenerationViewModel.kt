package com.bysoftware.aaeksen.presentation.video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.*
import com.bysoftware.aaeksen.data.firebase.repository.NewsRepository
import com.bysoftware.aaeksen.data.firebase.repository.VideoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoGenerationViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoGenerationUiState>(VideoGenerationUiState.Loading)
    val uiState: StateFlow<VideoGenerationUiState> = _uiState.asStateFlow()

    private val _generationRequests = MutableStateFlow<List<VideoGenerationRequest>>(emptyList())
    val generationRequests: StateFlow<List<VideoGenerationRequest>> = _generationRequests.asStateFlow()

    private val _availableNews = MutableStateFlow<List<FirebaseNews>>(emptyList())
    val availableNews: StateFlow<List<FirebaseNews>> = _availableNews.asStateFlow()

    private val _showNewsDialog = MutableStateFlow(false)
    val showNewsDialog: StateFlow<Boolean> = _showNewsDialog.asStateFlow()

    private val _showConfigDialog = MutableStateFlow(false)
    val showConfigDialog: StateFlow<Boolean> = _showConfigDialog.asStateFlow()

    private val _currentConfig = MutableStateFlow(VideoGenerationConfig())
    val currentConfig: StateFlow<VideoGenerationConfig> = _currentConfig.asStateFlow()

    private val _selectedNews = MutableStateFlow<FirebaseNews?>(null)
    val selectedNews: StateFlow<FirebaseNews?> = _selectedNews.asStateFlow()

    fun loadUserRequests() {
        viewModelScope.launch {
            _uiState.value = VideoGenerationUiState.Loading
            try {
                // Mock kullanıcı ID'si kullanıyoruz
                val result = videoRepository.getUserVideoRequests("sample_user_123")
                result.onSuccess { requests ->
                    _generationRequests.value = requests
                    _uiState.value = VideoGenerationUiState.Success
                    Log.d("VideoGenerationViewModel", "✅ ${requests.size} video isteği yüklendi")
                }.onFailure { error ->
                    _uiState.value = VideoGenerationUiState.Error(error.message ?: "İstekler yüklenemedi")
                    Log.e("VideoGenerationViewModel", "❌ İstekler yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                _uiState.value = VideoGenerationUiState.Error(e.message ?: "Beklenmeyen hata")
                Log.e("VideoGenerationViewModel", "❌ Beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun loadAvailableNews() {
        viewModelScope.launch {
            try {
                val result = newsRepository.getNews(limit = 50)
                result.onSuccess { newsList ->
                    _availableNews.value = newsList
                    Log.d("VideoGenerationViewModel", "✅ ${newsList.size} haber yüklendi")
                }.onFailure { error ->
                    Log.e("VideoGenerationViewModel", "❌ Haberler yüklenirken hata: ${error.message}")
                }
            } catch (e: Exception) {
                Log.e("VideoGenerationViewModel", "❌ Haberler yüklenirken beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun showNewsSelectionDialog() {
        _showNewsDialog.value = true
    }

    fun hideNewsSelectionDialog() {
        _showNewsDialog.value = false
    }

    fun showConfigDialog() {
        _showConfigDialog.value = true
    }

    fun hideConfigDialog() {
        _showConfigDialog.value = false
    }

    fun createVideoRequest(newsId: String) {
        val news = _availableNews.value.find { it.id == newsId }
        if (news != null) {
            _selectedNews.value = news
            hideNewsSelectionDialog()
            showConfigDialog()
        }
    }

    fun updateConfig(config: VideoGenerationConfig) {
        _currentConfig.value = config
    }

    fun confirmVideoCreation() {
        val news = _selectedNews.value
        if (news != null) {
            viewModelScope.launch {
                try {
                    val result = videoRepository.requestVideoGeneration(
                        newsId = news.id,
                        userId = "sample_user_123", // Mock kullanıcı ID'si
                        config = _currentConfig.value
                    )
                    
                    result.onSuccess { requestId ->
                        Log.d("VideoGenerationViewModel", "✅ Video oluşturma isteği gönderildi: $requestId")
                        hideConfigDialog()
                        _selectedNews.value = null
                        _currentConfig.value = VideoGenerationConfig() // Reset config
                        
                        // Real-time tracking başlat
                        startStatusPolling(requestId)
                        
                        // İstekleri yeniden yükle
                        loadUserRequests()
                    }.onFailure { error ->
                        Log.e("VideoGenerationViewModel", "❌ Video isteği gönderilirken hata: ${error.message}")
                    }
                } catch (e: Exception) {
                    Log.e("VideoGenerationViewModel", "❌ Video isteği gönderilirken beklenmeyen hata: ${e.message}")
                }
            }
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            try {
                // TODO: Video oluşturma isteğini iptal etme API'si
                // Şimdilik sadece status'u güncelliyoruz
                Log.d("VideoGenerationViewModel", "🚫 Video isteği iptal edildi: $requestId")
                
                // İstekleri yeniden yükle
                loadUserRequests()
            } catch (e: Exception) {
                Log.e("VideoGenerationViewModel", "❌ İstek iptal edilirken hata: ${e.message}")
            }
        }
    }

    fun retryRequest(requestId: String) {
        viewModelScope.launch {
            try {
                // TODO: Video oluşturma isteğini yeniden başlatma API'si
                Log.d("VideoGenerationViewModel", "🔄 Video isteği yeniden başlatıldı: $requestId")
                
                // İstekleri yeniden yükle
                loadUserRequests()
            } catch (e: Exception) {
                Log.e("VideoGenerationViewModel", "❌ İstek yeniden başlatılırken hata: ${e.message}")
            }
        }
    }

    fun refresh() {
        loadUserRequests()
        loadAvailableNews()
    }

    // Gerçek zamanlı durum güncellemeleri için
    fun startStatusPolling(requestId: String) {
        viewModelScope.launch {
            try {
                Log.d("VideoGenerationViewModel", "📡 Durum takibi başlatıldı: $requestId")
                
                // Firebase real-time listener
                val docRef = FirebaseFirestore.getInstance()
                    .collection("video_generation_requests")
                    .document(requestId)
                
                docRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("VideoGenerationViewModel", "❌ Listener hatası: ${error.message}")
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null && snapshot.exists()) {
                        val request = snapshot.toObject(VideoGenerationRequest::class.java)
                        request?.let { updatedRequest ->
                            // UI'ı güncelle
                            updateRequestInList(updatedRequest)
                            Log.d("VideoGenerationViewModel", "🔄 Status güncellendi: ${updatedRequest.status} (${updatedRequest.progress}%)")
                        }
                    }
                }
                
            } catch (e: Exception) {
                Log.e("VideoGenerationViewModel", "❌ Durum takibi başlatılırken hata: ${e.message}")
            }
        }
    }
    
    private fun updateRequestInList(updatedRequest: VideoGenerationRequest) {
        val currentRequests = _generationRequests.value.toMutableList()
        val index = currentRequests.indexOfFirst { it.id == updatedRequest.id }
        
        if (index != -1) {
            currentRequests[index] = updatedRequest
            _generationRequests.value = currentRequests
        }
    }

    fun stopStatusPolling(requestId: String) {
        // TODO: Polling'i durdur
        Log.d("VideoGenerationViewModel", "⏹️ Durum takibi durduruldu: $requestId")
    }
}

sealed class VideoGenerationUiState {
    object Loading : VideoGenerationUiState()
    object Success : VideoGenerationUiState()
    data class Error(val message: String) : VideoGenerationUiState()
}
