package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Ana Haber Modeli
 * Tüm haber özelliklerini içerir
 */
data class FirebaseNews(
    @DocumentId val id: String = "",
    
    // Temel Bilgiler
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val publishedAt: Timestamp? = null,
    
    // Görsel Bilgileri
    val imageUrl: String = "",
    val imageCaption: String = "", // Görseli açıklayan başlık
    
    // Kategorileme
    val category: String = "", // Spor, Siyaset, Ekonomi, vb.
    val timeTunnelCategory: String? = null, // Zaman tüneli kategorisi (varsa)
    
    // Konum Bilgileri
    val location: String = "", // İl adı (örn: "İstanbul", "Ankara")
    val coordinates: GeoPoint? = null, // Enlem/boylam
    
    // Oyunlaştırma
    val xpPoints: Int = 10,
    val likeCount: Long = 0,
    
    // AI Özellikleri
    val shortSummary: String = "", // Kısa özet
    val mediumSummary: String = "", // Orta boy özet
    val podcastVersion: String = "", // Podcast tarzı yazılmış versiyon
    
    // GeoGuessr Oyunu İçin
    val mapGuessDetail: String = "", // Konum bilgileri sansürlenmiş haber içeriği
    
    // Çengel Bulmaca Kelimeleri
    val crosswordWords: List<String> = emptyList(), // Haber içinden alınmış kelimeler
    
    // Quiz Soruları
    val quizQuestions: List<QuizQuestion> = emptyList(),
    
    // Meta Veriler
    val newsUrl: String = "", // Paylaşım için haberin URL'si
    val viewCount: Long = 0,
    val shareCount: Long = 0,
    val breaking: Boolean = false,
    val verified: Boolean = true, // isVerified -> verified
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Konum Bilgisi
 */
data class GeoPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

/**
 * Quiz Sorusu Modeli
 */
data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(), // 4 şık
    val correctAnswerIndex: Int = 0, // Doğru cevap index (0-3)
    val explanation: String = "", // Açıklama
    val xpPoints: Int = 5
)

/**
 * Zaman Tüneli Kategorisi
 * Örnek: "Ekrem İmamoğlu Tutuklanma Süreci"
 */
data class TimeTunnelCategory(
    @DocumentId val id: String = "",
    val title: String = "", // "Ekrem İmamoğlu Tutuklanma Süreci"
    val description: String = "",
    val coverImageUrl: String = "",
    val newsIds: List<String> = emptyList(), // Kronolojik sırada haber ID'leri
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)
