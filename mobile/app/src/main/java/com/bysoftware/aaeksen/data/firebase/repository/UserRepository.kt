package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Kullanıcı işlemleri için Repository
 * Profil, görevler, rozetler ve XP sistemi
 */
@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    // ==================== KULLANICI PROFİLİ ====================
    
    /**
     * Kullanıcı profilini getir
     */
    suspend fun getUserProfile(userId: String): Result<FirebaseUser?> {
        return try {
            val user = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .get()
                .await()
                .toObject(FirebaseUser::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mevcut kullanıcının profilini getir
     */
    suspend fun getCurrentUserProfile(): Result<FirebaseUser?> {
        val currentUser = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return getUserProfile(currentUser.uid)
    }

    /**
     * Mevcut kullanıcıyı getir (ProfileViewModel için)
     */
    suspend fun getCurrentUser(): Result<FirebaseUser?> {
        // Gerçek auth olmadan mock kullanıcı döndür
        return try {
            val mockUser = firestore.collection(FirebaseConfig.Collections.USERS)
                .document("sample_user_123")
                .get()
                .await()
                .toObject(FirebaseUser::class.java)
            Result.success(mockUser)
        } catch (e: Exception) {
            // Eğer Firebase'de yoksa mock kullanıcı oluştur
            Result.success(createMockUser())
        }
    }

    /**
     * Mock kullanıcı oluştur (test için)
     */
    private fun createMockUser(): FirebaseUser {
        return FirebaseUser(
            uid = "sample_user_123",
            email = "kamelia@example.com",
            username = "Kamelia Ahmed",
            photoUrl = "https://images.unsplash.com/photo-1494790108755-2616b332c913?w=150&h=150&fit=crop&crop=face",
            newsReadCount = 45,
            newsSharedCount = 12,
            totalXp = 222,
            badges = listOf(
                UserBadge(
                    badgeId = "confident_reader",
                    name = "Confident\nReader",
                    description = "Read 10 news articles",
                    iconUrl = "",
                    rarity = "common"
                ),
                UserBadge(
                    badgeId = "responsible_reader",
                    name = "Responsible\nReader",
                    description = "Share 5 news articles",
                    iconUrl = "",
                    rarity = "rare"
                ),
                UserBadge(
                    badgeId = "serious_learner",
                    name = "Serious\nLearner",
                    description = "Complete 3 quizzes",
                    iconUrl = "",
                    rarity = "epic"
                )
            ),
            isPremium = true,
            notificationsEnabled = true,
            darkModeEnabled = false
        )
    }
    
    /**
     * Kullanıcı profili oluştur (ilk kayıt)
     */
    suspend fun createUserProfile(
        userId: String,
        email: String,
        username: String
    ): Result<Unit> {
        return try {
            val user = FirebaseUser(
                uid = userId,
                email = email,
                username = username,
                createdAt = com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .set(user)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcı profilini güncelle
     */
    suspend fun updateUserProfile(
        userId: String,
        updates: Map<String, Any>
    ): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== XP SİSTEMİ ====================
    
    /**
     * Kullanıcıya XP ekle
     */
    suspend fun addXP(userId: String, xpAmount: Int): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .update("totalXp", com.google.firebase.firestore.FieldValue.increment(xpAmount.toLong()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Haber okundu - istatistik ve XP güncelle
     */
    suspend fun incrementNewsRead(userId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .update(
                    "newsReadCount", com.google.firebase.firestore.FieldValue.increment(1),
                    "totalXp", com.google.firebase.firestore.FieldValue.increment(FirebaseConfig.XP.NEWS_READ.toLong())
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Haber paylaşıldı - istatistik ve XP güncelle
     */
    suspend fun incrementNewsShared(userId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .update(
                    "newsSharedCount", com.google.firebase.firestore.FieldValue.increment(1),
                    "totalXp", com.google.firebase.firestore.FieldValue.increment(FirebaseConfig.XP.NEWS_SHARE.toLong())
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== GÜNLÜK GÖREVLER ====================
    
    /**
     * Aktif günlük görevleri getir
     */
    suspend fun getDailyTasks(): Result<List<DailyTask>> {
        return try {
            val tasks = firestore.collection(FirebaseConfig.Collections.DAILY_TASKS)
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", com.google.firebase.Timestamp.now())
                .get()
                .await()
                .toObjects(DailyTask::class.java)
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Kullanıcının görev ilerlemesini getir
     */
    suspend fun getUserTaskProgress(userId: String): Result<List<UserTaskProgress>> {
        return try {
            val progress = firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("isCompleted", false)
                .get()
                .await()
                .toObjects(UserTaskProgress::class.java)
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Görev ilerlemesini güncelle
     */
    suspend fun updateTaskProgress(
        userId: String,
        taskId: String,
        incrementBy: Int = 1
    ): Result<Boolean> {
        return try {
            // Önce görev bilgilerini al
            val task = firestore.collection(FirebaseConfig.Collections.DAILY_TASKS)
                .document(taskId)
                .get()
                .await()
                .toObject(DailyTask::class.java) ?: return Result.success(false)
            
            // İlerleme dokümanı ID'si
            val progressDocId = "${userId}_${taskId}"
            val progressRef = firestore.collection(FirebaseConfig.Collections.USER_TASK_PROGRESS)
                .document(progressDocId)
            
            var isCompleted = false
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(progressRef)
                
                if (snapshot.exists()) {
                    val current = snapshot.toObject(UserTaskProgress::class.java)
                    val newCount = (current?.currentCount ?: 0) + incrementBy
                    
                    if (newCount >= task.targetCount && current?.isCompleted == false) {
                        // Görev tamamlandı!
                        transaction.update(progressRef,
                            "currentCount", newCount,
                            "isCompleted", true,
                            "completedAt", com.google.firebase.Timestamp.now()
                        )
                        isCompleted = true
                    } else {
                        transaction.update(progressRef, "currentCount", newCount)
                    }
                } else {
                    // Yeni ilerleme oluştur
                    val newProgress = UserTaskProgress(
                        id = progressDocId,
                        userId = userId,
                        taskId = taskId,
                        currentCount = incrementBy,
                        targetCount = task.targetCount,
                        isCompleted = incrementBy >= task.targetCount,
                        completedAt = if (incrementBy >= task.targetCount) com.google.firebase.Timestamp.now() else null
                    )
                    transaction.set(progressRef, newProgress)
                    isCompleted = incrementBy >= task.targetCount
                }
            }.await()
            
            // Görev tamamlandıysa XP ver
            if (isCompleted) {
                addXP(userId, task.xpReward)
            }
            
            Result.success(isCompleted)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== ROZETLER ====================
    
    /**
     * Kullanıcıya rozet ver
     */
    suspend fun awardBadge(userId: String, badgeId: String): Result<Unit> {
        return try {
            // Rozet bilgilerini al
            val badge = firestore.collection(FirebaseConfig.Collections.BADGES)
                .document(badgeId)
                .get()
                .await()
                .toObject(Badge::class.java) ?: return Result.failure(Exception("Badge not found"))
            
            // Kullanıcının mevcut rozetlerini al
            val user = firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .get()
                .await()
                .toObject(FirebaseUser::class.java) ?: return Result.failure(Exception("User not found"))
            
            // Zaten varsa tekrar ekleme
            if (user.badges.any { it.badgeId == badgeId }) {
                return Result.success(Unit)
            }
            
            // Yeni rozet ekle
            val newBadge = UserBadge(
                badgeId = badgeId,
                name = badge.name,
                description = badge.description,
                iconUrl = badge.iconUrl,
                earnedAt = com.google.firebase.Timestamp.now(),
                rarity = badge.rarity
            )
            
            firestore.collection(FirebaseConfig.Collections.USERS)
                .document(userId)
                .update(
                    "badges", com.google.firebase.firestore.FieldValue.arrayUnion(newBadge),
                    "totalXp", com.google.firebase.firestore.FieldValue.increment(badge.xpReward.toLong())
                )
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Rozet gereksinimlerini kontrol et ve otomatik ver
     */
    suspend fun checkAndAwardBadges(userId: String): Result<List<String>> {
        return try {
            val user = getUserProfile(userId).getOrNull() ?: return Result.success(emptyList())
            val gameStats = firestore.collection(FirebaseConfig.Collections.USER_GAME_STATS)
                .document(userId)
                .get()
                .await()
                .toObject(UserGameStats::class.java)
            
            val allBadges = getAllBadges().getOrNull() ?: return Result.success(emptyList())
            val awardedBadges = mutableListOf<String>()
            
            for (badge in allBadges) {
                // Zaten varsa atla
                if (user.badges.any { it.badgeId == badge.id }) continue
                
                // Gereksinim kontrolü
                val requirement = badge.requirement ?: continue
                val meetsRequirement = when (requirement.type) {
                    "news_read" -> user.newsReadCount >= requirement.count
                    "news_shared" -> user.newsSharedCount >= requirement.count
                    "xp_earned" -> user.totalXp >= requirement.count
                    "quiz_solved" -> (gameStats?.quizCorrect ?: 0) >= requirement.count
                    "crossword_solved" -> (gameStats?.crosswordSolved ?: 0) >= requirement.count
                    "map_guess_correct" -> (gameStats?.mapGuessCorrect ?: 0) >= requirement.count
                    else -> false
                }
                
                if (meetsRequirement) {
                    awardBadge(userId, badge.id)
                    awardedBadges.add(badge.id)
                }
            }
            
            Result.success(awardedBadges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tüm rozetleri getir
     */
    suspend fun getAllBadges(): Result<List<Badge>> {
        return try {
            val badges = firestore.collection(FirebaseConfig.Collections.BADGES)
                .whereEqualTo("isActive", true)
                .get()
                .await()
                .toObjects(Badge::class.java)
            Result.success(badges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
