package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Çengel Bulmaca Kategorisi
 * Örnek: Siyaset, Ekonomi, Spor, Gündem
 */
data class CrosswordCategory(
    @DocumentId val id: String = "",
    val name: String = "", // "Siyaset", "Ekonomi", vb.
    val description: String = "",
    val iconUrl: String = "",
    val xpPoints: Int = 50, // Kategori tamamlama puanı
    val wordGroups: List<String> = emptyList(), // WordGroup ID'leri
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Kelime Grubu (Her kategoride 10'ar kelime)
 */
data class CrosswordWordGroup(
    @DocumentId val id: String = "",
    val categoryId: String = "",
    val words: List<CrosswordWord> = emptyList(), // 10 kelime
    val difficulty: String = "medium", // easy, medium, hard
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Tek Kelime
 */
data class CrosswordWord(
    val word: String = "",
    val hint: String = "",
    val xpPoints: Int = 5
)

/**
 * Quiz Kategorisi
 */
data class QuizCategory(
    @DocumentId val id: String = "",
    val name: String = "", // "Siyaset", "Ekonomi", vb.
    val description: String = "",
    val iconUrl: String = "",
    val questionIds: List<String> = emptyList(), // Quiz soru ID'leri
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Quiz Sorusu (Bağımsız Oyun İçin)
 */
data class QuizGameQuestion(
    @DocumentId val id: String = "",
    val categoryId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(), // 4 şık
    val correctAnswerIndex: Int = 0,
    val explanation: String = "",
    val xpPoints: Int = 10,
    val difficulty: String = "medium",
    val newsReference: String? = null, // İlgili haber ID'si (varsa)
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Harita Tahmin Oyunu
 * Normal haberlerden oluşturulan oyunlaştırılmış versiyonlar
 */
data class MapGuessGame(
    @DocumentId val id: String = "",
    val newsId: String = "", // Kaynak haber ID'si
    val title: String = "",
    val content: String = "", // Konum bilgileri sansürlenmiş içerik (mapGuessDetail)
    val imageUrl: String = "",
    val correctLocation: String = "", // İl adı
    val correctCoordinates: GeoPoint? = null,
    val xpPoints: Int = 20,
    val difficulty: String = "medium",
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

/**
 * Kullanıcı Oyun İstatistikleri
 */
data class UserGameStats(
    @DocumentId val userId: String = "",
    val crosswordSolved: Int = 0,
    val quizCorrect: Int = 0,
    val quizWrong: Int = 0,
    val mapGuessCorrect: Int = 0,
    val mapGuessWrong: Int = 0,
    val totalXp: Long = 0,
    val badges: List<String> = emptyList(),
    @ServerTimestamp val updatedAt: Timestamp? = null
)
