package com.bysoftware.aaeksen.data.mapper

import com.bysoftware.aaeksen.data.remote.dto.YouTubeVideoItemDto
import com.bysoftware.aaeksen.data.remote.dto.VideoDetailsItemDto
import com.bysoftware.aaeksen.domain.model.VideoShort
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object VideoMapper {
    
    /**
     * Convert YouTube API DTO to domain VideoShort model
     */
    fun YouTubeVideoItemDto.toDomain(
        statistics: VideoDetailsItemDto? = null
    ): VideoShort? {
        val videoId = id.videoId ?: return null
        
        return VideoShort(
            id = videoId,
            title = snippet.title,
            description = snippet.description,
            videoUrl = "https://www.youtube.com/watch?v=$videoId",
            thumbnailUrl = snippet.thumbnails.high.url,
            duration = statistics?.contentDetails?.duration ?: "PT30S",
            publishedAt = parsePublishedDate(snippet.publishedAt),
            viewCount = statistics?.statistics?.viewCount?.toLongOrNull() ?: 0L,
            likeCount = statistics?.statistics?.likeCount?.toLongOrNull() ?: 0L,
            channelId = snippet.channelId,
            channelTitle = snippet.channelTitle,
            tags = snippet.tags ?: extractTags(snippet.description),
            isLiked = false, // Will be set from local preferences
            isDisliked = false, // Will be set from local preferences
            isSaved = false // Will be set from local preferences
        )
    }
    
    /**
     * Parse YouTube API date format to LocalDateTime
     */
    private fun parsePublishedDate(publishedAt: String): LocalDateTime {
        return try {
            // YouTube API returns dates in ISO 8601 format: "2023-12-01T10:30:00Z"
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            LocalDateTime.parse(publishedAt, formatter)
        } catch (e: DateTimeParseException) {
            try {
                // Try alternative format without 'Z'
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime.parse(publishedAt, formatter)
            } catch (e2: DateTimeParseException) {
                // Fallback to current time if parsing fails
                LocalDateTime.now()
            }
        }
    }
    
    /**
     * Extract relevant tags from video description
     */
    private fun extractTags(description: String): List<String> {
        val commonNewsTags = listOf("haber", "güncel", "türkiye", "dünya", "siyaset", "ekonomi", "spor")
        val descriptionLower = description.lowercase()
        
        return commonNewsTags.filter { tag ->
            descriptionLower.contains(tag)
        }
    }
    
    /**
     * Format view count for display
     */
    fun formatViewCount(count: Long): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }
    
    /**
     * Format duration from ISO 8601 format (PT1M30S) to readable format
     */
    fun formatDuration(duration: String): String {
        return try {
            // Parse ISO 8601 duration format (PT1M30S)
            val regex = Regex("PT(?:(\\d+)M)?(?:(\\d+)S)?")
            val matchResult = regex.find(duration)
            
            if (matchResult != null) {
                val minutes = matchResult.groupValues[1].toIntOrNull() ?: 0
                val seconds = matchResult.groupValues[2].toIntOrNull() ?: 0
                
                when {
                    minutes > 0 -> String.format("%d:%02d", minutes, seconds)
                    else -> "${seconds}s"
                }
            } else {
                "0:30" // Default duration
            }
        } catch (e: Exception) {
            "0:30" // Default duration on error
        }
    }
    
    /**
     * Calculate time ago from LocalDateTime
     */
    fun calculateTimeAgo(publishedAt: LocalDateTime): String {
        val now = LocalDateTime.now()
        val duration = java.time.Duration.between(publishedAt, now)
        
        return when {
            duration.toDays() > 0 -> "${duration.toDays()} gün önce"
            duration.toHours() > 0 -> "${duration.toHours()} saat önce"
            duration.toMinutes() > 0 -> "${duration.toMinutes()} dakika önce"
            else -> "Az önce"
        }
    }
}