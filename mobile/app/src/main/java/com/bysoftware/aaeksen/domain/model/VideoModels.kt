package com.bysoftware.aaeksen.domain.model

import java.time.LocalDateTime

/**
 * YouTube Shorts video model for news content
 */
data class VideoShort(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: String, // Format: "PT1M30S" (1 minute 30 seconds)
    val publishedAt: LocalDateTime,
    val viewCount: Long,
    val likeCount: Long,
    val channelId: String,
    val channelTitle: String,
    val tags: List<String> = emptyList(),
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false
)

/**
 * Video interaction types
 */
enum class VideoInteractionType {
    LIKE,
    DISLIKE,
    SAVE,
    SHARE,
    GO_TO_NEWS
}

/**
 * Video interaction event
 */
data class VideoInteraction(
    val videoId: String,
    val type: VideoInteractionType,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * Video playback state
 */
data class VideoPlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Shorts feed state
 */
data class ShortsFeedState(
    val videos: List<VideoShort> = emptyList(),
    val currentVideoIndex: Int = 0,
    val isLoading: Boolean = false,
    val hasMoreVideos: Boolean = true,
    val error: String? = null
) {
    val currentVideo: VideoShort?
        get() = videos.getOrNull(currentVideoIndex)
}
