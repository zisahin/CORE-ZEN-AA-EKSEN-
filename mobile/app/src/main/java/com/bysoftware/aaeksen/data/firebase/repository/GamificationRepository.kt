package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Oyunlaştırma sistemi için Repository
 * XP, rozetler, günlük görevler ve kullanıcı ilerlemesi
 */
@Singleton
class GamificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // ==================== KULLANICI PROFİLİ ====================

    /**
     * Kullanıcı profilini getir (users collection'dan)
     */
    suspend fun getUserProfile(userId: String? = null): Result<FirebaseUser?> {
        return try {
            val uid = userId ?: auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            val profile = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(uid)
                .get()
                .await()
                .toObject(FirebaseUser::class.java)
            
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kullanıcı profili oluştur veya güncelle (users collection'da)
     */
    suspend fun createOrUpdateUserProfile(profile: FirebaseUser): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(uid)
                .set(profile)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== XP SİSTEMİ ====================

    /**
     * Kullanıcıya XP ekle (users collection'da totalXp field'ını güncelle)
     */
    suspend fun addXP(xpAmount: Int, source: String, description: String = ""): Result<FirebaseUser?> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            // Mevcut profili al
            val currentProfile = getUserProfile(uid).getOrNull() ?: FirebaseUser(
                uid = uid,
                username = auth.currentUser?.displayName ?: "Kullanıcı",
                email = auth.currentUser?.email ?: ""
            )
            
            // XP ekle
            val newXP = currentProfile.totalXp + xpAmount
            
            // Firebase'de totalXp field'ını güncelle
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(uid)
                .update(
                    "totalXp", newXP,
                    "updatedAt", Timestamp.now()
                )
                .await()
            
            // Güncellenmiş profili al
            val updatedProfile = currentProfile.copy(
                totalXp = newXP,
                updatedAt = Timestamp.now()
            )
            
            // Rozet kontrolü yap
            checkBadgeRequirements(uid, source, newXP)
            
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * XP'den seviye hesapla
     */
    private fun calculateLevel(totalXP: Int): Int {
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
            else -> 10 + (totalXP - 5500) / 1000
        }
    }

    /**
     * Rozet gereksinimlerini kontrol et (badges collection'daki requirement'lara göre)
     */
    private suspend fun checkBadgeRequirements(userId: String, source: String, totalXP: Long) {
        try {
            // Badges collection'dan aktif rozetleri al
            val badges = firestore.collection(FirebaseConfig.Collections.BADGES)
                .whereEqualTo("active", true)
                .get()
                .await()
            
            for (badgeDoc in badges.documents) {
                val badge = badgeDoc.toObject(Badge::class.java)
                if (badge != null) {
                    val requirement = badge.requirement
                    
                    // Rozet gereksinimini kontrol et
                    val shouldAward = when (requirement["type"] as? String) {
                        "news_read" -> {
                            val currentCount = getCurrentUserCount(userId, "newsReadCount")
                            currentCount >= (requirement["count"] as? Long ?: 0)
                        }
                        "total_xp" -> {
                            totalXP >= (requirement["count"] as? Long ?: 0)
                        }
                        "crossword_solved" -> {
                            val currentCount = getCurrentUserCount(userId, "crosswordSolved")
                            currentCount >= (requirement["count"] as? Long ?: 0)
                        }
                        else -> false
                    }
                    
                    if (shouldAward) {
                        awardBadgeToUser(userId, badgeDoc.id, badge.name)
                    }
                }
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et
        }
    }
    
    /**
     * Kullanıcının belirli bir field'daki sayısını al
     */
    private suspend fun getCurrentUserCount(userId: String, field: String): Long {
        return try {
            val userDoc = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .get()
                .await()
            
            userDoc.getLong(field) ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    // ==================== ROZET SİSTEMİ ====================

    /**
     * Kullanıcıya rozet ver (users collection'daki badges array'ine ekle)
     */
    suspend fun awardBadgeToUser(userId: String, badgeId: String, badgeName: String): Result<Unit> {
        return try {
            // Kullanıcının mevcut rozetlerini al
            val userDoc = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .get()
                .await()
            
            val currentBadges = userDoc.get("badges") as? List<Map<String, Any>> ?: emptyList()
            
            // Rozet zaten var mı kontrol et
            val hasBadge = currentBadges.any { badge ->
                badge["badgeId"] == badgeId
            }
            
            if (!hasBadge) {
                // Yeni rozet oluştur
                val newBadge = mapOf(
                    "badgeId" to badgeId,
                    "name" to badgeName,
                    "earnedAt" to Timestamp.now()
                )
                
                // Badges array'ine ekle
                val updatedBadges = currentBadges + newBadge
                
                firestore.collection(FirebaseConfig.Collections.USERS)
                    .document(userId)
                    .update("badges", updatedBadges)
                    .await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kullanıcının rozetlerini getir (users collection'daki badges array'inden)
     */
    suspend fun getUserBadges(userId: String? = null): Result<List<com.bysoftware.aaeksen.data.firebase.model.UserBadge>> {
        return try {
            val uid = userId ?: auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            val userDoc = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(uid)
                .get()
                .await()
            
            val badgesData = userDoc.get("badges") as? List<Map<String, Any>> ?: emptyList()
            
            val badges = badgesData.map { badgeMap ->
                com.bysoftware.aaeksen.data.firebase.model.UserBadge(
                    badgeId = badgeMap["badgeId"] as? String ?: "",
                    name = badgeMap["name"] as? String ?: "",
                    description = badgeMap["description"] as? String ?: "",
                    iconUrl = badgeMap["iconUrl"] as? String ?: "",
                    earnedAt = badgeMap["earnedAt"] as? Timestamp,
                    rarity = badgeMap["rarity"] as? String ?: "common"
                )
            }
            
            Result.success(badges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== GÜNLÜK GÖREVLER ====================

    /**
     * Günlük görevleri getir
     */
    suspend fun getDailyTasks(): Result<List<DailyTask>> {
        return try {
            val tasks = firestore.collection(FirebaseConfig.Collections.DAILY_TASKS)
                .whereEqualTo("active", true)
                .get()
                .await()
                .toObjects(DailyTask::class.java)
            
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kullanıcının günlük görev ilerlemesini getir
     */
    suspend fun getUserTaskProgress(userId: String? = null): Result<List<UserTaskProgress>> {
        return try {
            val uid = userId ?: auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            // Bugünün tarihi
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            
            val progress = firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                .whereEqualTo("userId", uid)
                .whereEqualTo("date", today)
                .get()
                .await()
                .toObjects(UserTaskProgress::class.java)
            
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Görev ilerlemesi güncelle
     */
    suspend fun updateTaskProgress(taskId: String, increment: Int = 1): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(java.util.Date())
            
            // Mevcut ilerlemeyi bul
            val existingProgress = firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                .whereEqualTo("userId", uid)
                .whereEqualTo("taskId", taskId)
                .whereEqualTo("date", today)
                .get()
                .await()
            
            if (existingProgress.isEmpty) {
                // Yeni ilerleme oluştur
                val progress = UserTaskProgress(
                    userId = uid,
                    taskId = taskId,
                    date = today,
                    currentProgress = increment,
                    isCompleted = false,
                    updatedAt = Timestamp.now()
                )
                
                firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                    .add(progress)
                    .await()
            } else {
                // Mevcut ilerlemeyi güncelle
                val doc = existingProgress.documents[0]
                val currentProgress = doc.getLong("currentProgress")?.toInt() ?: 0
                val newProgress = currentProgress + increment
                
                firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                    .document(doc.id)
                    .update(
                        "currentProgress", newProgress,
                        "updatedAt", Timestamp.now()
                    )
                    .await()
                
                // Görev tamamlanma kontrolü
                checkTaskCompletion(uid, taskId, newProgress)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Görev tamamlanma kontrolü
     */
    private suspend fun checkTaskCompletion(userId: String, taskId: String, currentProgress: Int) {
        try {
            // Görev detaylarını al
            val task = firestore.collection(FirebaseConfig.Collections.DAILY_TASKS)
                .document(taskId)
                .get()
                .await()
                .toObject(DailyTask::class.java)
            
            if (task != null && currentProgress >= task.targetCount) {
                // Görevi tamamlandı olarak işaretle
                val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
                
                firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("taskId", taskId)
                    .whereEqualTo("date", today)
                    .get()
                    .await()
                    .documents[0]
                    .reference
                    .update("isCompleted", true)
                    .await()
                
                // XP ödülü ver
                addXP(task.xpReward, "daily_task", "Günlük görev: ${task.title}")
                
                // Rozet kontrolü
                if (task.badgeReward.isNotEmpty()) {
                    awardBadgeToUser(userId, task.badgeReward, task.title)
                }
            }
        } catch (e: Exception) {
            // Hata durumunda sessizce devam et
        }
    }

    // ==================== İSTATİSTİKLER ====================

    /**
     * Kullanıcı istatistiklerini getir
     */
    suspend fun getUserStats(userId: String? = null): Result<UserStats> {
        return try {
            val uid = userId ?: auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            val stats = firestore.collection(FirebaseConfig.Collections.USER_STATS)
                .document(uid)
                .get()
                .await()
                .toObject(UserStats::class.java) ?: UserStats(userId = uid)
            
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * İstatistik güncelle (users collection'daki field'ları güncelle)
     */
    suspend fun updateStats(statType: String, increment: Int = 1): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı giriş yapmamış"))
            
            val userRef = firestore.collection(FirebaseConfig.Collections.USERS).document(uid)
            
            when (statType) {
                "news_read" -> userRef.update("newsReadCount", com.google.firebase.firestore.FieldValue.increment(increment.toLong()))
                "news_shared" -> userRef.update("newsSharedCount", com.google.firebase.firestore.FieldValue.increment(increment.toLong()))
                "shares" -> userRef.update("newsSharedCount", com.google.firebase.firestore.FieldValue.increment(increment.toLong()))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== LIDERLIK TABLOSU ====================

    /**
     * Liderlik tablosunu getir
     */
    suspend fun getLeaderboard(limit: Long = 50): Result<List<UserProfile>> {
        return try {
            val leaders = firestore.collection(FirebaseConfig.Collections.USER_PROFILES)
                .orderBy("totalXP", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(UserProfile::class.java)
            
            Result.success(leaders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * XP Geçmişi modeli
 */
data class XPHistory(
    val id: String = "",
    val userId: String = "",
    val xpAmount: Int = 0,
    val source: String = "", // "news_read", "video_watched", "game_completed", etc.
    val description: String = "",
    val timestamp: Timestamp? = null
)

/**
 * Kullanıcı rozeti modeli
 */
data class UserBadge(
    val id: String = "",
    val userId: String = "",
    val badgeId: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val earnedAt: Timestamp? = null
)

/**
 * Kullanıcı görev ilerlemesi
 */
data class UserTaskProgress(
    val id: String = "",
    val userId: String = "",
    val taskId: String = "",
    val date: String = "", // YYYY-MM-DD format
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

/**
 * Kullanıcı istatistikleri
 */
data class UserStats(
    val id: String = "",
    val userId: String = "",
    val newsReadCount: Long = 0,
    val videosWatchedCount: Long = 0,
    val gamesPlayedCount: Long = 0,
    val shareCount: Long = 0,
    val commentCount: Long = 0,
    val totalLoginDays: Long = 0,
    val lastLoginDate: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)
