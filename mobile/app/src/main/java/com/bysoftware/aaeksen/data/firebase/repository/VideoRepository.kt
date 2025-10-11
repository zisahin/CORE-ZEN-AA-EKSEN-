package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Video işlemleri için Repository
 * Video oluşturma, oynatma ve yönetim işlemlerini yönetir
 */
@Singleton
class VideoRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ==================== VİDEO İÇERİKLERİ ====================

    /**
     * Tüm videoları getir
     */
    suspend fun getVideos(
        category: String? = null,
        limit: Long = 20,
        lastVideoId: String? = null
    ): Result<List<VideoContent>> {
        return try {
            var query = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .whereEqualTo("isActive", true)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)

            // Kategori filtresi
            if (category != null) {
                query = query.whereEqualTo("category", category)
            }

            // Pagination için son video ID'si
            if (lastVideoId != null) {
                val lastDoc = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                    .document(lastVideoId)
                    .get()
                    .await()
                query = query.startAfter(lastDoc)
            }

            val videos = query.get().await().toObjects(VideoContent::class.java)
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Tek video detayını getir
     */
    suspend fun getVideoById(videoId: String): Result<VideoContent?> {
        return try {
            val video = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .document(videoId)
                .get()
                .await()
                .toObject(VideoContent::class.java)
            Result.success(video)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Öne çıkan videoları getir
     */
    suspend fun getFeaturedVideos(limit: Long = 10): Result<List<VideoContent>> {
        return try {
            val videos = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .whereEqualTo("isFeatured", true)
                .whereEqualTo("isActive", true)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(VideoContent::class.java)
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Haber ID'sine göre videoları getir
     */
    suspend fun getVideosByNewsId(newsId: String): Result<List<VideoContent>> {
        return try {
            val videos = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .whereEqualTo("newsId", newsId)
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(VideoContent::class.java)
            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Video görüntülenme sayısını artır
     */
    suspend fun incrementViewCount(videoId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .document(videoId)
                .update("viewCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Video beğeni sayısını artır
     */
    suspend fun incrementLikeCount(videoId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .document(videoId)
                .update("likeCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Video paylaşma sayısını artır
     */
    suspend fun incrementShareCount(videoId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .document(videoId)
                .update("shareCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== VİDEO OLUŞTURMA ====================

    /**
     * Video oluşturma isteği gönder
     */
    suspend fun requestVideoGeneration(
        newsId: String,
        userId: String,
        config: VideoGenerationConfig = VideoGenerationConfig()
    ): Result<String> {
        return try {
            val request = VideoGenerationRequest(
                newsId = newsId,
                userId = userId,
                requestType = VideoRequestType.MANUAL,
                config = config,
                status = VideoGenerationStatus.PENDING,
                createdAt = com.google.firebase.Timestamp.now()
            )

            val docRef = firestore.collection(FirebaseConfig.Collections.VIDEO_GENERATION_REQUESTS)
                .add(request)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Video oluşturma durumunu getir
     */
    suspend fun getVideoGenerationStatus(requestId: String): Result<VideoGenerationRequest?> {
        return try {
            val request = firestore.collection(FirebaseConfig.Collections.VIDEO_GENERATION_REQUESTS)
                .document(requestId)
                .get()
                .await()
                .toObject(VideoGenerationRequest::class.java)
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Kullanıcının video oluşturma isteklerini getir
     */
    suspend fun getUserVideoRequests(userId: String): Result<List<VideoGenerationRequest>> {
        return try {
            val requests = firestore.collection(FirebaseConfig.Collections.VIDEO_GENERATION_REQUESTS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(VideoGenerationRequest::class.java)
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== VİDEO OYNATMA LİSTELERİ ====================

    /**
     * Oynatma listelerini getir
     */
    suspend fun getPlaylists(limit: Long = 20): Result<List<VideoPlaylist>> {
        return try {
            val playlists = firestore.collection(FirebaseConfig.Collections.VIDEO_PLAYLISTS)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(VideoPlaylist::class.java)
            Result.success(playlists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resmi AA oynatma listelerini getir
     */
    suspend fun getOfficialPlaylists(): Result<List<VideoPlaylist>> {
        return try {
            val playlists = firestore.collection(FirebaseConfig.Collections.VIDEO_PLAYLISTS)
                .whereEqualTo("isOfficial", true)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(VideoPlaylist::class.java)
            Result.success(playlists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Oynatma listesi detayını getir
     */
    suspend fun getPlaylistById(playlistId: String): Result<VideoPlaylist?> {
        return try {
            val playlist = firestore.collection(FirebaseConfig.Collections.VIDEO_PLAYLISTS)
                .document(playlistId)
                .get()
                .await()
                .toObject(VideoPlaylist::class.java)
            Result.success(playlist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Oynatma listesindeki videoları getir
     */
    suspend fun getPlaylistVideos(playlistId: String): Result<List<VideoContent>> {
        return try {
            val playlist = getPlaylistById(playlistId).getOrNull()
                ?: return Result.success(emptyList())

            val videos = mutableListOf<VideoContent>()
            for (videoId in playlist.videoIds) {
                val video = getVideoById(videoId).getOrNull()
                if (video != null) {
                    videos.add(video)
                }
            }

            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== VİDEO YORUMLARI ====================

    /**
     * Video yorumlarını getir
     */
    suspend fun getVideoComments(
        videoId: String,
        limit: Long = 50
    ): Result<List<VideoComment>> {
        return try {
            val comments = firestore.collection(FirebaseConfig.Collections.VIDEO_COMMENTS)
                .whereEqualTo("videoId", videoId)
                .whereEqualTo("isDeleted", false)
                .whereEqualTo("parentCommentId", "") // Ana yorumlar
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(VideoComment::class.java)
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Yorum yanıtlarını getir
     */
    suspend fun getCommentReplies(commentId: String): Result<List<VideoComment>> {
        return try {
            val replies = firestore.collection(FirebaseConfig.Collections.VIDEO_COMMENTS)
                .whereEqualTo("parentCommentId", commentId)
                .whereEqualTo("isDeleted", false)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(VideoComment::class.java)
            Result.success(replies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Yorum ekle
     */
    suspend fun addComment(
        videoId: String,
        userId: String,
        username: String,
        userPhotoUrl: String,
        content: String,
        parentCommentId: String = ""
    ): Result<String> {
        return try {
            val comment = VideoComment(
                videoId = videoId,
                userId = userId,
                username = username,
                userPhotoUrl = userPhotoUrl,
                content = content,
                parentCommentId = parentCommentId,
                createdAt = com.google.firebase.Timestamp.now()
            )

            val docRef = firestore.collection(FirebaseConfig.Collections.VIDEO_COMMENTS)
                .add(comment)
                .await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==================== ARAMA ====================

    /**
     * Video arama
     */
    suspend fun searchVideos(query: String, limit: Long = 20): Result<List<VideoContent>> {
        return try {
            // Firestore'da tam metin araması olmadığı için title ve description'da arama yapıyoruz
            val videos = firestore.collection(FirebaseConfig.Collections.VIDEOS)
                .whereEqualTo("isActive", true)
                .whereEqualTo("isPublic", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .await()
                .toObjects(VideoContent::class.java)
                .filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                }
                .take(limit.toInt())

            Result.success(videos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
