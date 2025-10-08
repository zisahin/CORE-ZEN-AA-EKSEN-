package com.bysoftware.aaeksen.core.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * YouTube video URL extractor utility
 * Extracts direct stream URLs from YouTube video URLs
 */
object YouTubeExtractor {
    
    private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    
    /**
     * Extract video ID from YouTube URL (including Shorts)
     */
    fun extractVideoId(youtubeUrl: String): String? {
        val patterns = listOf(
            "(?<=watch\\?v=)[^&#]*",
            "(?<=youtu.be/)[^&#]*",
            "(?<=embed/)[^&#]*",
            "(?<=v/)[^&#]*",
            "(?<=shorts/)[^&#]*" // YouTube Shorts support
        )
        
        for (pattern in patterns) {
            val regex = Pattern.compile(pattern)
            val matcher = regex.matcher(youtubeUrl)
            if (matcher.find()) {
                return matcher.group()
            }
        }
        return null
    }
    
    /**
     * Create YouTube Shorts URL
     */
    fun createShortsUrl(videoId: String): String {
        return "https://www.youtube.com/shorts/$videoId"
    }
    
    /**
     * Check if URL is a YouTube Shorts URL
     */
    fun isShortsUrl(url: String): Boolean {
        return url.contains("/shorts/")
    }
    
    /**
     * Get direct stream URL for YouTube video
     * This is a simplified version - in production, use a proper YouTube extractor library
     */
    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            // For demo purposes, we'll return a placeholder stream URL
            // In a real app, you would use a proper YouTube extractor
            
            // YouTube doesn't allow direct access to video streams anymore
            // The best approach is to use YouTube's embed player or redirect to YouTube app
            
            // Return null to indicate we should use the embed approach
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Create YouTube embed URL
     */
    fun createEmbedUrl(videoId: String): String {
        return "https://www.youtube.com/embed/$videoId?autoplay=1&controls=1&rel=0&modestbranding=1&playsinline=1"
    }
    
    /**
     * Create YouTube watch URL
     */
    fun createWatchUrl(videoId: String): String {
        return "https://www.youtube.com/watch?v=$videoId"
    }
}
