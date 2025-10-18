package com.bysoftware.aaeksen.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.data.firebase.model.AIQuestion
import com.bysoftware.aaeksen.data.firebase.repository.NewsRepository
import com.google.firebase.firestore.FirebaseFirestore
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

    private val _dailyAIQuestion = MutableStateFlow<AIQuestion?>(null)
    val dailyAIQuestion: StateFlow<AIQuestion?> = _dailyAIQuestion.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()

    init {
        loadNews()
        loadDailyAIQuestion()
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
        loadDailyAIQuestion()
    }

    private fun loadDailyAIQuestion() {
        viewModelScope.launch {
            try {
                firestore.collection("ai_questions")
                    .whereEqualTo("isActive", true)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val doc = documents.documents[0]
                            val question = AIQuestion(
                                id = doc.id,
                                question = doc.getString("question") ?: "",
                                options = doc.get("options") as? List<String> ?: emptyList(),
                                category = doc.getString("category") ?: "",
                                isActive = doc.getBoolean("isActive") ?: true
                            )
                            _dailyAIQuestion.value = question
                            Log.d("HomeViewModel", "✅ AI sorusu yüklendi: ${question.question}")
                        }
                    }
                    .addOnFailureListener { error ->
                        Log.e("HomeViewModel", "❌ AI sorusu yükleme hatası: ${error.message}")
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ AI sorusu beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun submitAIAnswer(questionId: String, selectedOption: String) {
        viewModelScope.launch {
            try {
                val answer = hashMapOf(
                    "questionId" to questionId,
                    "userId" to "anonymous", // Şimdilik anonymous
                    "selectedOption" to selectedOption,
                    "timestamp" to System.currentTimeMillis()
                )
                
                firestore.collection("ai_question_answers")
                    .add(answer)
                    .addOnSuccessListener {
                        Log.d("HomeViewModel", "✅ AI cevabı gönderildi")
                        // Soruyu gizle
                        _dailyAIQuestion.value = null
                    }
                    .addOnFailureListener { error ->
                        Log.e("HomeViewModel", "❌ AI cevabı gönderme hatası: ${error.message}")
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "❌ AI cevabı beklenmeyen hata: ${e.message}")
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
