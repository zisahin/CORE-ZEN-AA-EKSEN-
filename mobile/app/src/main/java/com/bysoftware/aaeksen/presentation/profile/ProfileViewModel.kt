package com.bysoftware.aaeksen.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.FirebaseUser
import com.bysoftware.aaeksen.data.firebase.model.Badge
import com.bysoftware.aaeksen.data.firebase.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Profil ekranı için ViewModel
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _allBadges = MutableStateFlow<List<Badge>>(emptyList())
    val allBadges: StateFlow<List<Badge>> = _allBadges.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                // Kullanıcı profilini yükle
                userRepository.getCurrentUser()
                    .onSuccess { fetchedUser ->
                        _user.value = fetchedUser
                        Log.d("ProfileViewModel", "✅ Kullanıcı profili yüklendi: ${fetchedUser?.username}")
                    }
                    .onFailure { error ->
                        Log.e("ProfileViewModel", "❌ Kullanıcı profili yüklenemedi: ${error.message}")
                    }

                // Tüm rozetleri yükle
                userRepository.getAllBadges()
                    .onSuccess { badges ->
                        _allBadges.value = badges
                        Log.d("ProfileViewModel", "✅ Rozetler yüklendi: ${badges.size} adet")
                    }
                    .onFailure { error ->
                        Log.e("ProfileViewModel", "❌ Rozetler yüklenemedi: ${error.message}")
                    }

                _uiState.value = ProfileUiState.Success

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Beklenmeyen hata")
                Log.e("ProfileViewModel", "❌ Profil yüklenirken beklenmeyen hata: ${e.message}")
            }
        }
    }

    fun refresh() {
        loadUserProfile()
    }

    /**
     * Kullanıcının kazandığı rozet sayısını hesapla
     */
    fun getEarnedBadgesCount(): Int {
        return _user.value?.badges?.size ?: 0
    }

    /**
     * Toplam rozet sayısını hesapla
     */
    fun getTotalBadgesCount(): Int {
        return _allBadges.value.size
    }

    /**
     * Kullanıcının okuma süresini hesapla (mock)
     * Gerçek uygulamada bu veri Firebase'de tutulabilir
     */
    fun getReadingTime(): String {
        val newsCount = _user.value?.newsReadCount ?: 0
        val estimatedHours = newsCount * 3 // Her haber için ortalama 3 dakika
        return "${estimatedHours / 60}h ${estimatedHours % 60}m"
    }

    /**
     * Tamamlanan bölüm sayısını hesapla (mock)
     */
    fun getCompletedChapters(): Int {
        return (_user.value?.newsReadCount ?: 0) / 10 // Her 10 haberde bir bölüm
    }

    /**
     * Tamamlanan hikaye sayısını hesapla (mock)
     */
    fun getCompletedStories(): Int {
        return (_user.value?.newsReadCount ?: 0) / 5 // Her 5 haberde bir hikaye
    }

    /**
     * Toplam pratik sayısını hesapla (mock)
     */
    fun getTotalPractices(): Int {
        return (_user.value?.newsReadCount ?: 0) + (_user.value?.newsSharedCount ?: 0)
    }
}

/**
 * Profil ekranı UI durumları
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    object Success : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
