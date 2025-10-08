package com.bysoftware.aaeksen.data.firebase.repository

import com.bysoftware.aaeksen.core.constants.FirebaseConfig
import com.bysoftware.aaeksen.data.firebase.model.FirebaseNews
import com.bysoftware.aaeksen.data.firebase.model.TimeTunnelCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Haber işlemleri için Repository
 * Ana haberler, zaman tüneli ve haber haritası işlemlerini yönetir
 */
@Singleton
class NewsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    
    // ==================== ANA HABERLER ====================
    
    /**
     * Tüm haberleri getir (kategori filtreli)
     */
    suspend fun getNews(
        category: String? = null,
        limit: Long = 20,
        lastNewsId: String? = null
    ): Result<List<FirebaseNews>> {
        return try {
            var query = firestore.collection(FirebaseConfig.Collections.NEWS)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(limit)
            
            // Kategori filtresi
            if (category != null) {
                query = query.whereEqualTo("category", category)
            }
            
            // Pagination için son haber ID'si
            if (lastNewsId != null) {
                val lastDoc = firestore.collection(FirebaseConfig.Collections.NEWS)
                    .document(lastNewsId)
                    .get()
                    .await()
                query = query.startAfter(lastDoc)
            }
            
            val news = query.get().await().toObjects(FirebaseNews::class.java)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Tek haber detayını getir
     */
    suspend fun getNewsById(newsId: String): Result<FirebaseNews?> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .document(newsId)
                .get()
                .await()
                .toObject(FirebaseNews::class.java)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Önemli (breaking) haberleri getir
     */
    suspend fun getBreakingNews(limit: Long = 5): Result<List<FirebaseNews>> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .whereEqualTo("breaking", true)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Haber görüntülenme sayısını artır
     */
    suspend fun incrementViewCount(newsId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.NEWS)
                .document(newsId)
                .update("viewCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Haber beğeni sayısını artır
     */
    suspend fun incrementLikeCount(newsId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.NEWS)
                .document(newsId)
                .update("likeCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Haber paylaşma sayısını artır
     */
    suspend fun incrementShareCount(newsId: String): Result<Unit> {
        return try {
            firestore.collection(FirebaseConfig.Collections.NEWS)
                .document(newsId)
                .update("shareCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== ZAMAN TÜNELİ ====================
    
    /**
     * Zaman tüneli kategorilerini getir
     */
    suspend fun getTimeTunnelCategories(): Result<List<TimeTunnelCategory>> {
        return try {
            val categories = firestore.collection(FirebaseConfig.Collections.TIME_TUNNEL_CATEGORIES)
                .whereEqualTo("active", true)
                // .orderBy("createdAt", Query.Direction.DESCENDING) // Sıralama sorgusunu kaldır
                .get()
                .await()
                .toObjects(TimeTunnelCategory::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Zaman tüneli kategorisine ait haberleri kronolojik sırada getir
     */
    suspend fun getTimeTunnelNews(categoryId: String): Result<List<FirebaseNews>> {
        return try {
            // Önce kategoriyi getir
            val category = firestore.collection(FirebaseConfig.Collections.TIME_TUNNEL_CATEGORIES)
                .document(categoryId)
                .get()
                .await()
                .toObject(TimeTunnelCategory::class.java)
            
            if (category == null) {
                return Result.success(emptyList())
            }
            
            // Haber ID'lerine göre haberleri getir (sıralı)
            val newsList = mutableListOf<FirebaseNews>()
            for (newsId in category.newsIds) {
                val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                    .document(newsId)
                    .get()
                    .await()
                    .toObject(FirebaseNews::class.java)
                
                if (news != null) {
                    newsList.add(news)
                }
            }
            
            Result.success(newsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HABER HARİTASI ====================
    
    /**
     * İl bazında haber sayılarını getir
     * Haber haritası için il yoğunluğu hesaplaması
     */
    suspend fun getNewsByLocation(location: String): Result<List<FirebaseNews>> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .whereEqualTo("location", location)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * İl ve kategori bazında haberleri getir
     * Haber haritasında kategoriye tıklayınca kullanılır
     */
    suspend fun getNewsByLocationAndCategory(
        location: String,
        category: String,
        limit: Long = 20
    ): Result<List<FirebaseNews>> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .whereEqualTo("location", location)
                .whereEqualTo("category", category)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * İl bazında haber kategorilerini ve sayılarını getir
     */
    suspend fun getCategoriesByLocation(location: String): Result<Map<String, Int>> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .whereEqualTo("location", location)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
            
            // Kategorilere göre grupla ve say
            val categoryCount = news.groupBy { it.category }
                .mapValues { it.value.size }
            
            Result.success(categoryCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Tüm illerdeki haber sayılarını getir
     * Haber haritası renklendirmesi için
     */
    suspend fun getAllLocationsWithCount(): Result<Map<String, Int>> {
        return try {
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
            
            // İllere göre grupla ve say
            val locationCount = news.groupBy { it.location }
                .mapValues { it.value.size }
                .filter { it.key.isNotEmpty() }
            
            Result.success(locationCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== ARAMA ====================
    
    /**
     * Başlık veya içerik ile arama yap
     * Not: Gerçek uygulamada Algolia gibi bir arama servisi kullanılabilir
     */
    suspend fun searchNews(query: String, limit: Long = 20): Result<List<FirebaseNews>> {
        return try {
            // Firestore'da tam metin araması olmadığı için tüm haberleri çekip filtrelemeliyiz
            // Gerçek uygulamada Algolia, Elasticsearch gibi servisleri kullanmalısınız
            val news = firestore.collection(FirebaseConfig.Collections.NEWS)
                .orderBy("publishedAt", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .await()
                .toObjects(FirebaseNews::class.java)
                .filter {
                    it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
                }
                .take(limit.toInt())
            
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Son haberleri getir
     */
    suspend fun getLatestNews(limit: Long = 20): Result<List<FirebaseNews>> {
        return getNews(category = null, limit = limit, lastNewsId = null)
    }
    
    /**
     * Kategoriye göre haberleri getir
     */
    suspend fun getNewsByCategory(category: String, limit: Long = 20): Result<List<FirebaseNews>> {
        return getNews(category = category, limit = limit, lastNewsId = null)
    }
    
    /**
     * Haber beğenisini artır
     */
    suspend fun incrementLikes(newsId: String): Result<Unit> {
        return incrementLikeCount(newsId)
    }
}
