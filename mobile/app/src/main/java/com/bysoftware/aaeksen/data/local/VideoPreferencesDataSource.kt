package com.bysoftware.aaeksen.data.local

import com.bysoftware.aaeksen.domain.model.VideoInteraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for video preferences and interactions
 * In a real app, this would use Room database or DataStore
 */
@Singleton
class VideoPreferencesDataSource @Inject constructor() {

    // In-memory storage for demo purposes
    // In real app, use Room database or DataStore
    private val likedVideos = mutableSetOf<String>()
    private val dislikedVideos = mutableSetOf<String>()
    private val savedVideos = mutableSetOf<String>()
    private val interactions = mutableListOf<VideoInteraction>()

    private val _likedVideosFlow = MutableStateFlow(likedVideos.toSet())
    val likedVideosFlow: StateFlow<Set<String>> = _likedVideosFlow.asStateFlow()

    private val _savedVideosFlow = MutableStateFlow(savedVideos.toSet())
    val savedVideosFlow: StateFlow<Set<String>> = _savedVideosFlow.asStateFlow()

    /**
     * Check if video is liked
     */
    fun isVideoLiked(videoId: String): Boolean {
        return likedVideos.contains(videoId)
    }

    /**
     * Check if video is disliked
     */
    fun isVideoDisliked(videoId: String): Boolean {
        return dislikedVideos.contains(videoId)
    }

    /**
     * Check if video is saved
     */
    fun isVideoSaved(videoId: String): Boolean {
        return savedVideos.contains(videoId)
    }

    /**
     * Toggle like status for a video
     */
    suspend fun toggleLike(videoId: String) {
        if (likedVideos.contains(videoId)) {
            likedVideos.remove(videoId)
        } else {
            likedVideos.add(videoId)
            // Remove from disliked if it was disliked
            dislikedVideos.remove(videoId)
        }
        _likedVideosFlow.value = likedVideos.toSet()
    }

    /**
     * Toggle dislike status for a video
     */
    suspend fun toggleDislike(videoId: String) {
        if (dislikedVideos.contains(videoId)) {
            dislikedVideos.remove(videoId)
        } else {
            dislikedVideos.add(videoId)
            // Remove from liked if it was liked
            likedVideos.remove(videoId)
        }
        _likedVideosFlow.value = likedVideos.toSet()
    }

    /**
     * Toggle save status for a video
     */
    suspend fun toggleSave(videoId: String) {
        if (savedVideos.contains(videoId)) {
            savedVideos.remove(videoId)
        } else {
            savedVideos.add(videoId)
        }
        _savedVideosFlow.value = savedVideos.toSet()
    }

    /**
     * Record user interaction with video
     */
    suspend fun recordInteraction(interaction: VideoInteraction) {
        interactions.add(interaction)
        // In real app, save to database
    }

    /**
     * Get all interactions for analytics
     */
    fun getInteractions(): List<VideoInteraction> {
        return interactions.toList()
    }

    /**
     * Get liked video IDs
     */
    fun getLikedVideoIds(): Set<String> {
        return likedVideos.toSet()
    }

    /**
     * Get saved video IDs
     */
    fun getSavedVideoIds(): Set<String> {
        return savedVideos.toSet()
    }

    /**
     * Clear all preferences (for testing or logout)
     */
    suspend fun clearAll() {
        likedVideos.clear()
        dislikedVideos.clear()
        savedVideos.clear()
        interactions.clear()
        _likedVideosFlow.value = emptySet()
        _savedVideosFlow.value = emptySet()
    }
}
