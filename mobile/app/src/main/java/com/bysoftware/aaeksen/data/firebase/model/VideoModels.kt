package com.bysoftware.aaeksen.data.firebase.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Video İçeriği Modeli
 * Otomatik oluşturulan haber videoları için
 */
data class VideoContent(
    @DocumentId val id: String = "",
    
    // Temel Bilgiler
    val title: String = "",
    val description: String = "",
    val newsId: String = "", // Hangi habere ait olduğu
    
    // Video Dosyası
    val videoUrl: String = "", // Firebase Storage'daki video URL'si
    val thumbnailUrl: String = "", // Video önizleme görseli
    val duration: Long = 0, // Saniye cinsinden süre
    val fileSize: Long = 0, // Byte cinsinden dosya boyutu
    
    // Video Kalitesi
    val resolution: String = "1080p", // 720p, 1080p, 4K
    val format: String = "mp4", // mp4, webm, avi
    val bitrate: Int = 0, // kbps cinsinden
    
    // İçerik Bilgileri
    val category: String = "", // Haber kategorisi
    val tags: List<String> = emptyList(), // Video etiketleri
    val language: String = "tr", // Dil kodu
    
    // Oluşturma Bilgileri
    val generationStatus: VideoGenerationStatus = VideoGenerationStatus.PENDING,
    val generationStartedAt: Timestamp? = null,
    val generationCompletedAt: Timestamp? = null,
    val generationDuration: Long = 0, // Oluşturma süresi (saniye)
    
    // AI Bilgileri
    val transcription: String = "", // Whisper ile oluşturulan transkripsiyon
    val audioFiles: List<String> = emptyList(), // Kullanılan ses dosyaları
    val imageFiles: List<String> = emptyList(), // Kullanılan görsel dosyaları
    val backgroundMusic: String = "", // Arka plan müziği
    val backgroundImage: String = "", // Arka plan görseli
    
    // İstatistikler
    val viewCount: Long = 0,
    val likeCount: Long = 0,
    val shareCount: Long = 0,
    val downloadCount: Long = 0,
    
    // Meta Veriler
    val isPublic: Boolean = true,
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Video Oluşturma Durumları
 */
enum class VideoGenerationStatus {
    PENDING,        // Beklemede
    PROCESSING,     // İşleniyor
    TRANSCRIBING,   // Transkripsiyon yapılıyor
    GENERATING,     // Video oluşturuluyor
    UPLOADING,      // Yükleniyor
    COMPLETED,      // Tamamlandı
    FAILED,         // Başarısız
    CANCELLED       // İptal edildi
}

/**
 * Video Oluşturma İsteği
 */
data class VideoGenerationRequest(
    @DocumentId val id: String = "",
    
    // İstek Bilgileri
    val newsId: String = "", // Hangi haber için video oluşturulacak
    val userId: String = "", // İsteği yapan kullanıcı
    val requestType: VideoRequestType = VideoRequestType.AUTO,
    
    // Yapılandırma
    val config: VideoGenerationConfig = VideoGenerationConfig(),
    
    // Durum Bilgileri
    val status: VideoGenerationStatus = VideoGenerationStatus.PENDING,
    val progress: Int = 0, // 0-100 arası ilerleme yüzdesi
    val currentStep: String = "", // Mevcut işlem adımı
    val errorMessage: String = "", // Hata mesajı (varsa)
    
    // Sonuç
    val resultVideoId: String = "", // Oluşturulan video ID'si
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Video İsteği Türleri
 */
enum class VideoRequestType {
    AUTO,           // Otomatik oluşturma
    MANUAL,         // Manuel istek
    SCHEDULED,      // Zamanlanmış
    BATCH           // Toplu işlem
}

/**
 * Video Oluşturma Yapılandırması
 */
data class VideoGenerationConfig(
    // Video Ayarları
    val resolution: String = "1080p",
    val format: String = "mp4",
    val quality: VideoQuality = VideoQuality.HIGH,
    
    // Ses Ayarları
    val includeBackgroundMusic: Boolean = true,
    val musicVolume: Float = 0.3f, // 0.0 - 1.0 arası
    val voiceVolume: Float = 0.8f,
    
    // Görsel Ayarları
    val includeImages: Boolean = true,
    val imageTransitionDuration: Float = 1.0f, // Saniye
    val useBackgroundImage: Boolean = true,
    
    // Metin Ayarları
    val includeSubtitles: Boolean = true,
    val subtitleLanguage: String = "tr",
    val fontFamily: String = "Arial",
    val fontSize: Int = 24,
    
    // İçerik Ayarları
    val maxDuration: Int = 300, // Maksimum süre (saniye)
    val includeIntro: Boolean = true,
    val includeOutro: Boolean = true,
    
    // AI Ayarları
    val useWhisperTranscription: Boolean = true,
    val whisperModel: String = "base", // tiny, base, small, medium, large
    val enhanceAudio: Boolean = true
)

/**
 * Video Kalite Seviyeleri
 */
enum class VideoQuality {
    LOW,        // 720p, düşük bitrate
    MEDIUM,     // 1080p, orta bitrate
    HIGH,       // 1080p, yüksek bitrate
    ULTRA       // 4K, en yüksek kalite
}

/**
 * Video İstatistikleri
 */
data class VideoStats(
    @DocumentId val id: String = "",
    val videoId: String = "",
    
    // Görüntüleme İstatistikleri
    val totalViews: Long = 0,
    val uniqueViews: Long = 0,
    val averageWatchTime: Long = 0, // Saniye
    val completionRate: Float = 0.0f, // 0.0 - 1.0 arası
    
    // Etkileşim İstatistikleri
    val likes: Long = 0,
    val dislikes: Long = 0,
    val shares: Long = 0,
    val comments: Long = 0,
    val downloads: Long = 0,
    
    // Zaman Bazlı İstatistikler
    val viewsByHour: Map<String, Long> = emptyMap(), // "00" -> view count
    val viewsByDay: Map<String, Long> = emptyMap(),   // "2024-01-01" -> view count
    val viewsByMonth: Map<String, Long> = emptyMap(), // "2024-01" -> view count
    
    // Demografik İstatistikler
    val viewsByCountry: Map<String, Long> = emptyMap(), // "TR" -> view count
    val viewsByCity: Map<String, Long> = emptyMap(),    // "Istanbul" -> view count
    val viewsByDevice: Map<String, Long> = emptyMap(),  // "mobile" -> view count
    
    @ServerTimestamp val lastUpdated: Timestamp? = null
)

/**
 * Video Yorumları
 */
data class VideoComment(
    @DocumentId val id: String = "",
    val videoId: String = "",
    val userId: String = "",
    val username: String = "",
    val userPhotoUrl: String = "",
    
    val content: String = "",
    val parentCommentId: String = "", // Yanıt ise ana yorum ID'si
    
    val likeCount: Long = 0,
    val dislikeCount: Long = 0,
    val replyCount: Long = 0,
    
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val isPinned: Boolean = false,
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

/**
 * Video Oynatma Listesi
 */
data class VideoPlaylist(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val thumbnailUrl: String = "",
    
    val videoIds: List<String> = emptyList(),
    val videoCount: Int = 0,
    val totalDuration: Long = 0, // Saniye
    
    val creatorId: String = "",
    val creatorName: String = "",
    
    val isPublic: Boolean = true,
    val isOfficial: Boolean = false, // AA resmi oynatma listesi mi?
    
    val category: String = "",
    val tags: List<String> = emptyList(),
    
    val viewCount: Long = 0,
    val subscriberCount: Long = 0,
    
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
