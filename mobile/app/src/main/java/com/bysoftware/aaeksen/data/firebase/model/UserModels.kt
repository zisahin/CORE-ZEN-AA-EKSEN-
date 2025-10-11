package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Gamification Kullanıcı Profil Modeli
 */
data class UserProfile(
    @DocumentId val id: String = "",
    
    // Temel Bilgiler
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    
    // XP ve Seviye Sistemi
    val totalXP: Int = 0,
    val level: Int = 1,
    
    // İstatistikler
    val newsReadCount: Long = 0,
    val videosWatchedCount: Long = 0,
    val gamesPlayedCount: Long = 0,
    val shareCount: Long = 0,
    val commentCount: Long = 0,
    
    // Rozetler
    val badgeCount: Int = 0,
    val completedTasksCount: Int = 0,
    
    // Premium
    val isPremium: Boolean = false,
    val premiumExpiryDate: Timestamp? = null,
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Kullanıcı Profil Modeli (Eski)
 */
data class FirebaseUser(
    @DocumentId val uid: String = "",
    
    // Temel Bilgiler
    val email: String = "",
    val username: String = "",
    val photoUrl: String = "",
    
    // İstatistikler
    val newsReadCount: Int = 0, // Okunan haber sayısı
    val newsSharedCount: Int = 0, // Paylaşma sayısı
    val totalXp: Long = 0, // Toplam XP puanı
    
    // Rozetler
    val badges: List<UserBadge> = emptyList(),
    
    // Premium
    val isPremium: Boolean = false,
    val premiumExpiryDate: Timestamp? = null,
    
    // Günlük Görevler
    val dailyTasks: List<String> = emptyList(), // Aktif görev ID'leri
    val completedTasks: List<String> = emptyList(), // Tamamlanan görev ID'leri
    
    // Ayarlar
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Kullanıcı Rozeti (users collection'daki badges array'inde)
 */
data class UserBadge(
    val badgeId: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val earnedAt: Timestamp? = null,
    val rarity: String = "common" // common, rare, epic, legendary
)

/**
 * Rozet Modeli (badges collection'dan)
 */
data class Badge(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val rarity: String = "common",
    val requirement: Map<String, Any> = emptyMap(), // type: "news_read", count: 10
    val xpReward: Int = 0,
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Günlük Görev
 */
data class DailyTask(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "", // read_news, share_news, solve_crossword, solve_quiz
    val targetCount: Int = 1, // Hedef sayı (örn: 2 haber paylaş)
    val xpReward: Int = 20,
    val badgeReward: String = "", // Rozet ödülü (varsa)
    val expiresAt: Timestamp? = null, // Görevin son geçerlilik tarihi
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Kullanıcı Görev İlerlemesi
 */
data class UserTaskProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val taskId: String = "",
    val currentCount: Int = 0,
    val targetCount: Int = 1,
    val isCompleted: Boolean = false,
    val completedAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

