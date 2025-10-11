package com.bysoftware.aaeksen.presentation.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bysoftware.aaeksen.data.firebase.model.*
import com.bysoftware.aaeksen.data.firebase.repository.GamificationRepository
import com.bysoftware.aaeksen.data.firebase.repository.UserBadge
import com.bysoftware.aaeksen.data.firebase.repository.UserStats
import com.bysoftware.aaeksen.data.firebase.repository.UserTaskProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val gamificationRepository: GamificationRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<FirebaseUser?>(null)
    val userProfile: StateFlow<FirebaseUser?> = _userProfile.asStateFlow()

    private val _userBadges = MutableStateFlow<List<com.bysoftware.aaeksen.data.firebase.model.UserBadge>>(emptyList())
    val userBadges: StateFlow<List<com.bysoftware.aaeksen.data.firebase.model.UserBadge>> = _userBadges.asStateFlow()

    private val _dailyTasks = MutableStateFlow<List<DailyTask>>(emptyList())
    val dailyTasks: StateFlow<List<DailyTask>> = _dailyTasks.asStateFlow()

    private val _taskProgress = MutableStateFlow<List<UserTaskProgress>>(emptyList())
    val taskProgress: StateFlow<List<UserTaskProgress>> = _taskProgress.asStateFlow()

    private val _userStats = MutableStateFlow<UserStats?>(null)
    val userStats: StateFlow<UserStats?> = _userStats.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<FirebaseUser>>(emptyList())
    val leaderboard: StateFlow<List<FirebaseUser>> = _leaderboard.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Kullanıcı verilerini yükle
     */
    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Profil yükle
                gamificationRepository.getUserProfile().onSuccess { profile ->
                    _userProfile.value = profile
                }

                // Rozetler yükle
                gamificationRepository.getUserBadges().onSuccess { badges ->
                    _userBadges.value = badges
                }

                // Günlük görevler yükle
                gamificationRepository.getDailyTasks().onSuccess { tasks ->
                    _dailyTasks.value = tasks
                }

                // Görev ilerlemesi yükle
                gamificationRepository.getUserTaskProgress().onSuccess { progress ->
                    _taskProgress.value = progress
                }

                // İstatistikler yükle
                gamificationRepository.getUserStats().onSuccess { stats ->
                    _userStats.value = stats
                }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * XP ekle
     */
    fun addXP(amount: Int, source: String, description: String = "") {
        viewModelScope.launch {
            gamificationRepository.addXP(amount, source, description).onSuccess { updatedProfile ->
                _userProfile.value = updatedProfile
                
                // Rozetleri yeniden yükle (yeni rozet kazanılmış olabilir)
                viewModelScope.launch {
                    gamificationRepository.getUserBadges().onSuccess { badges ->
                        _userBadges.value = badges
                    }
                }
            }.onFailure { error ->
                _error.value = error.message
            }
        }
    }

    /**
     * Haber okuma XP'si
     */
    fun onNewsRead() {
        addXP(10, "news_read", "Haber okudu")
        updateStats("news_read")
        updateTaskProgress("read_news")
    }

    /**
     * Video izleme XP'si
     */
    fun onVideoWatched() {
        addXP(15, "video_watched", "Video izledi")
        updateStats("videos_watched")
        updateTaskProgress("watch_videos")
    }

    /**
     * Paylaşım XP'si
     */
    fun onContentShared() {
        addXP(20, "content_shared", "İçerik paylaştı")
        updateStats("shares")
        updateTaskProgress("share_content")
    }

    /**
     * Yorum XP'si
     */
    fun onCommentMade() {
        addXP(5, "comment_made", "Yorum yaptı")
        updateStats("comments")
        updateTaskProgress("make_comments")
    }

    /**
     * Oyun XP'si
     */
    fun onGameCompleted(gameType: String, score: Int) {
        val xpAmount = when (gameType) {
            "crossword" -> 50
            "quiz" -> 25
            "map_guess" -> 30
            else -> 20
        }
        
        addXP(xpAmount, "game_completed", "$gameType oyunu tamamladı (Skor: $score)")
        updateStats("games_played")
        updateTaskProgress("play_games")
    }

    /**
     * İstatistik güncelle
     */
    private fun updateStats(statType: String) {
        viewModelScope.launch {
            gamificationRepository.updateStats(statType)
        }
    }

    /**
     * Görev ilerlemesi güncelle
     */
    private fun updateTaskProgress(taskId: String, increment: Int = 1) {
        viewModelScope.launch {
            gamificationRepository.updateTaskProgress(taskId, increment).onSuccess {
                // Görev ilerlemesini yeniden yükle
                gamificationRepository.getUserTaskProgress().onSuccess { progress ->
                    _taskProgress.value = progress
                }
            }
        }
    }

    /**
     * Liderlik tablosunu yükle
     */
    fun loadLeaderboard() {
        viewModelScope.launch {
            // Leaderboard şimdilik devre dışı - UserProfile'dan FirebaseUser'a dönüşüm gerekiyor
            // gamificationRepository.getLeaderboard().onSuccess { leaders ->
            //     _leaderboard.value = leaders
            // }.onFailure { error ->
            //     _error.value = error.message
            // }
        }
    }

    /**
     * Hatayı temizle
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Seviye için gereken XP hesapla
     */
    fun getXPForNextLevel(currentLevel: Int): Long {
        return when (currentLevel) {
            1 -> 100L
            2 -> 300L
            3 -> 600L
            4 -> 1000L
            5 -> 1500L
            6 -> 2100L
            7 -> 2800L
            8 -> 3600L
            9 -> 4500L
            10 -> 5500L
            else -> 5500L + (currentLevel - 10) * 1000L
        }
    }

    /**
     * Mevcut seviyedeki ilerleme yüzdesi
     */
    fun getLevelProgress(currentXP: Long, currentLevel: Int): Float {
        val currentLevelXP = if (currentLevel == 1) 0L else getXPForNextLevel(currentLevel - 1)
        val nextLevelXP = getXPForNextLevel(currentLevel)
        val progressXP = currentXP - currentLevelXP
        val requiredXP = nextLevelXP - currentLevelXP
        
        return if (requiredXP > 0) progressXP.toFloat() / requiredXP.toFloat() else 1f
    }
    
    /**
     * XP'den seviye hesapla
     */
    fun calculateLevelFromXP(totalXP: Long): Int {
        return when {
            totalXP < 100 -> 1
            totalXP < 300 -> 2
            totalXP < 600 -> 3
            totalXP < 1000 -> 4
            totalXP < 1500 -> 5
            totalXP < 2100 -> 6
            totalXP < 2800 -> 7
            totalXP < 3600 -> 8
            totalXP < 4500 -> 9
            totalXP < 5500 -> 10
            else -> 10 + ((totalXP - 5500) / 1000).toInt()
        }
    }

    /**
     * Günlük görev tamamlanma durumu
     */
    fun getTaskCompletionStatus(taskId: String): Pair<Int, Boolean> {
        val progress = _taskProgress.value.find { it.taskId == taskId }
        val task = _dailyTasks.value.find { it.id == taskId }
        
        return if (progress != null && task != null) {
            Pair(progress.currentProgress, progress.isCompleted)
        } else {
            Pair(0, false)
        }
    }

    /**
     * Toplam tamamlanan görev sayısı
     */
    fun getCompletedTasksCount(): Int {
        return _taskProgress.value.count { it.isCompleted }
    }

    /**
     * Bugünkü toplam XP
     */
    fun getTodayXP(): Long {
        // Bu basit bir hesaplama, gerçekte XP geçmişinden bugünkü XP'yi hesaplayabilirsin
        return _taskProgress.value.sumOf { progress -> if (progress.isCompleted) 100L else 0L }
    }
}
