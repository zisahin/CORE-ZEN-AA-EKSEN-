package com.bysoftware.aaeksen.domain.repository

import androidx.paging.PagingData
import com.bysoftware.aaeksen.domain.model.VideoInteraction
import com.bysoftware.aaeksen.domain.model.VideoShort
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for video-related operations
 */
interface VideoRepository {
    
    /**
     * Get paginated YouTube Shorts from Anadolu Ajansı channel
     */
    fun getYouTubeShorts(): Flow<PagingData<VideoShort>>
    
    /**
     * Get a specific video by ID
     */
    suspend fun getVideoById(videoId: String): Result<VideoShort>
    
    /**
     * Record user interaction with video
     */
    suspend fun recordInteraction(interaction: VideoInteraction): Result<Unit>
    
    /**
     * Toggle like status for a video
     */
    suspend fun toggleLike(videoId: String): Result<VideoShort>
    
    /**
     * Toggle dislike status for a video
     */
    suspend fun toggleDislike(videoId: String): Result<VideoShort>
    
    /**
     * Toggle save status for a video
     */
    suspend fun toggleSave(videoId: String): Result<VideoShort>
    
    /**
     * Get user's liked videos
     */
    fun getLikedVideos(): Flow<PagingData<VideoShort>>
    
    /**
     * Get user's saved videos
     */
    fun getSavedVideos(): Flow<PagingData<VideoShort>>
    
    /**
     * Search videos by query
     */
    fun searchVideos(query: String): Flow<PagingData<VideoShort>>
    
    /**
     * Get video statistics (views, likes, etc.)
     */
    suspend fun getVideoStatistics(videoId: String): Result<Map<String, Long>>
}
