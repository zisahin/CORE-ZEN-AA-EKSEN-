package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Kullanıcı Profil Modeli
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
 * Kullanıcı Rozeti
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
 * Günlük Görev
 */
data class DailyTask(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "", // read_news, share_news, solve_crossword, solve_quiz
    val targetCount: Int = 1, // Hedef sayı (örn: 2 haber paylaş)
    val xpReward: Int = 20,
    val expiresAt: Timestamp? = null, // Görevin son geçerlilik tarihi
    val isActive: Boolean = true,
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

/**
 * Rozet Sistemi (Tüm Rozetler)
 */
data class Badge(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val rarity: String = "common",
    val requirement: BadgeRequirement? = null,
    val xpReward: Int = 0,
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Rozet Gereksinimleri
 */
data class BadgeRequirement(
    val type: String = "", // news_read, quiz_solved, xp_earned, vb.
    val count: Int = 0
)
