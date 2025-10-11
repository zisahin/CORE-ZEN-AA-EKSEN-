package com.bysoftware.aaeksen.presentation.timetunnel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.TimeTunnelCategory
import com.bysoftware.aaeksen.data.firebase.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews

@HiltViewModel
class TimeTunnelViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeTunnelUiState>(TimeTunnelUiState.Loading)
    val uiState: StateFlow<TimeTunnelUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<TimeTunnelCategory>>(emptyList())
    val categories: StateFlow<List<TimeTunnelCategory>> = _categories.asStateFlow()

    private val _newsList = MutableStateFlow<List<FirebaseNews>>(emptyList())
    val newsList: StateFlow<List<FirebaseNews>> = _newsList.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = TimeTunnelUiState.Loading
            newsRepository.getTimeTunnelCategories()
                .onSuccess { categoryList ->
                    _categories.value = categoryList
                    _uiState.value = TimeTunnelUiState.Success
                    Log.d("TimeTunnelViewModel", "✅ ${categoryList.size} zaman tüneli kategorisi yüklendi.")
                }
                .onFailure { error ->
                    _uiState.value = TimeTunnelUiState.Error(error.message ?: "Kategoriler yüklenemedi.")
                    Log.e("TimeTunnelViewModel", "❌ Zaman tüneli kategorileri yüklenirken hata: ${error.message}")
                }
        }
    }

    fun loadTimeTunnelNews(categoryId: String) {
        viewModelScope.launch {
            _uiState.value = TimeTunnelUiState.Loading
            newsRepository.getTimeTunnelNews(categoryId)
                .onSuccess { news ->
                    _newsList.value = news
                    _uiState.value = TimeTunnelUiState.Success
                    Log.d("TimeTunnelViewModel", "✅ ${news.size} adet zaman tüneli haberi yüklendi.")
                }
                .onFailure { error ->
                     _uiState.value = TimeTunnelUiState.Error(error.message ?: "Haberler yüklenemedi.")
                    Log.e("TimeTunnelViewModel", "❌ Zaman tüneli haberleri yüklenirken hata: ${error.message}")
                }
        }
    }
}

sealed class TimeTunnelUiState {
    object Loading : TimeTunnelUiState()
    object Success : TimeTunnelUiState()
    data class Error(val message: String) : TimeTunnelUiState()
}
