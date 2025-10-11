package com.bysoftware.aaeksen.presentation.home

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
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _breakingNews = MutableStateFlow<List<FirebaseNews>>(emptyList())
    val breakingNews: StateFlow<List<FirebaseNews>> = _breakingNews.asStateFlow()

    private val _recommendedNews = MutableStateFlow<List<FirebaseNews>>(emptyList())
    val recommendedNews: StateFlow<List<FirebaseNews>> = _recommendedNews.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            try {
                // Breaking news (isBreaking == true olanlar)
                newsRepository.getBreakingNews(limit = 5)
                    .onSuccess { news ->
                        _breakingNews.value = news
                        Log.d("HomeViewModel", "✅ ${news.size} breaking news yüklendi")
                    }
                    .onFailure { error ->
                        Log.e("HomeViewModel", "❌ Breaking news hatası: ${error.message}")
                        _breakingNews.value = emptyList()
                    }

                // Son haberler
                newsRepository.getLatestNews(limit = 20)
                    .onSuccess { news ->
                        // Yinelenenleri filtrele
                        val breakingNewsIds = _breakingNews.value.map { it.id }.toSet()
                        _recommendedNews.value = news.filter { it.id !in breakingNewsIds }
                        _uiState.value = HomeUiState.Success
                        Log.d("HomeViewModel", "✅ ${news.size} haber yüklendi, yinelenenler filtrelendi.")
                    }
                    .onFailure { error ->
                        Log.e("HomeViewModel", "❌ Haber yükleme hatası: ${error.message}")
                        _uiState.value = HomeUiState.Success
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ Beklenmeyen hata: ${e.message}", e)
                _uiState.value = HomeUiState.Success
            }
        }
    }

    fun loadNewsByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            newsRepository.getNewsByCategory(category, limit = 20)
                .onSuccess { news ->
                    val breakingNewsIds = _breakingNews.value.map { it.id }.toSet()
                    _recommendedNews.value = news.filter { it.id !in breakingNewsIds }
                    _uiState.value = HomeUiState.Success
                    Log.d("HomeViewModel", "✅ ${news.size} $category haberi yüklendi")
                }
                .onFailure { error ->
                    Log.e("HomeViewModel", "❌ Kategori hatası: ${error.message}")
                    _uiState.value = HomeUiState.Success
                }
        }
    }

    fun refresh() {
        loadNews()
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
