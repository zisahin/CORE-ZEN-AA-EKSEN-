package com.bysoftware.aaeksen.presentation.newsdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.data.firebase.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsDetailUiState>(NewsDetailUiState.Loading)
    val uiState: StateFlow<NewsDetailUiState> = _uiState.asStateFlow()

    private val _news = MutableStateFlow<FirebaseNews?>(null)
    val news: StateFlow<FirebaseNews?> = _news.asStateFlow()

    fun loadNews(newsId: String) {
        viewModelScope.launch {
            _uiState.value = NewsDetailUiState.Loading
            
            newsRepository.getNewsById(newsId)
                .onSuccess { firebaseNews ->
                    _news.value = firebaseNews
                    _uiState.value = NewsDetailUiState.Success
                    Log.d("NewsDetailViewModel", "✅ Haber yüklendi: ${firebaseNews?.title}")
                }
                .onFailure { error ->
                    Log.e("NewsDetailViewModel", "❌ Haber yükleme hatası: ${error.message}")
                    // Kullanıcıya hata gösterme
                    _uiState.value = NewsDetailUiState.Success
                }
        }
    }

    fun incrementLike(newsId: String) {
        viewModelScope.launch {
            newsRepository.incrementLikes(newsId)
                .onSuccess {
                    Log.d("NewsDetailViewModel", "✅ Beğeni artırıldı")
                    // Like sayısı artırıldı, yeniden yükle
                    loadNews(newsId)
                }
                .onFailure { error ->
                    Log.e("NewsDetailViewModel", "❌ Beğeni hatası: ${error.message}")
                }
        }
    }

    fun updateXP(newsId: String) {
        viewModelScope.launch {
            // Kullanıcının bu haberi okuduğunu kaydet ve XP ekle
            // TODO: UserRepository'de implement edilecek
            Log.d("NewsDetailViewModel", "XP güncelleme: $newsId")
        }
    }
}

sealed class NewsDetailUiState {
    object Loading : NewsDetailUiState()
    object Success : NewsDetailUiState()
    data class Error(val message: String) : NewsDetailUiState()
}
