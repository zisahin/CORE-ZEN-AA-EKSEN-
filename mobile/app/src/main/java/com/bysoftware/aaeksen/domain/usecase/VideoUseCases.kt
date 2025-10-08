package com.bysoftware.aaeksen.domain.usecase

import androidx.paging.PagingData
import com.bysoftware.aaeksen.domain.model.VideoInteraction
import com.bysoftware.aaeksen.domain.model.VideoInteractionType
import com.bysoftware.aaeksen.domain.model.VideoShort
import com.bysoftware.aaeksen.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting YouTube Shorts from Anadolu Ajansı
 */
class GetYouTubeShortsUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<PagingData<VideoShort>> {
        return videoRepository.getYouTubeShorts()
    }
}

/**
 * Use case for handling video interactions (like, dislike, save, share)
 */
class HandleVideoInteractionUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(
        videoId: String,
        interactionType: VideoInteractionType
    ): Result<VideoShort> {
        return try {
            // Record the interaction
            val interaction = VideoInteraction(
                videoId = videoId,
                type = interactionType
            )
            videoRepository.recordInteraction(interaction)
            
            // Perform the specific action
            when (interactionType) {
                VideoInteractionType.LIKE -> videoRepository.toggleLike(videoId)
                VideoInteractionType.DISLIKE -> videoRepository.toggleDislike(videoId)
                VideoInteractionType.SAVE -> videoRepository.toggleSave(videoId)
                VideoInteractionType.SHARE -> {
                    // Share action doesn't modify the video, just return current state
                    videoRepository.getVideoById(videoId)
                }
                VideoInteractionType.GO_TO_NEWS -> {
                    // Navigation action, just return current state
                    videoRepository.getVideoById(videoId)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Use case for getting video by ID
 */
class GetVideoByIdUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: String): Result<VideoShort> {
        return videoRepository.getVideoById(videoId)
    }
}

/**
 * Use case for searching videos
 */
class SearchVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(query: String): Flow<PagingData<VideoShort>> {
        return videoRepository.searchVideos(query)
    }
}

/**
 * Use case for getting liked videos
 */
class GetLikedVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<PagingData<VideoShort>> {
        return videoRepository.getLikedVideos()
    }
}

/**
 * Use case for getting saved videos
 */
class GetSavedVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    operator fun invoke(): Flow<PagingData<VideoShort>> {
        return videoRepository.getSavedVideos()
    }
}
