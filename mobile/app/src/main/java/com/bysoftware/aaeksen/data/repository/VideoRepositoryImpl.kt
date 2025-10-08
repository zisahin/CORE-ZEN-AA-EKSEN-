package com.bysoftware.aaeksen.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bysoftware.aaeksen.data.local.VideoPreferencesDataSource
import com.bysoftware.aaeksen.data.mapper.VideoMapper.toDomain
import com.bysoftware.aaeksen.data.paging.YouTubeShortsPagingSource
import com.bysoftware.aaeksen.data.remote.YouTubeApiService
import com.bysoftware.aaeksen.domain.model.VideoInteraction
import com.bysoftware.aaeksen.domain.model.VideoShort
import com.bysoftware.aaeksen.domain.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(
    private val youTubeApiService: YouTubeApiService,
    private val videoPreferencesDataSource: VideoPreferencesDataSource
) : VideoRepository {

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
    }

    override fun getYouTubeShorts(): Flow<PagingData<VideoShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                YouTubeShortsPagingSource(
                    youTubeApiService = youTubeApiService,
                    videoPreferencesDataSource = videoPreferencesDataSource
                )
            }
        ).flow
    }

    override suspend fun getVideoById(videoId: String): Result<VideoShort> {
        return try {
            // For now, return a mock video. In real implementation, 
            // you would call YouTube API to get specific video details
            val mockVideo = VideoShort(
                id = videoId,
                title = "Sample News Video",
                description = "This is a sample news video description",
                videoUrl = "https://www.youtube.com/watch?v=$videoId",
                thumbnailUrl = "",
                duration = "PT30S",
                publishedAt = java.time.LocalDateTime.now(),
                viewCount = 1000L,
                likeCount = 50L,
                channelId = "UCDmYRnhMwphO_BHiPhOLVzw",
                channelTitle = "Anadolu Ajansı",
                tags = listOf("news", "breaking"),
                isLiked = videoPreferencesDataSource.isVideoLiked(videoId),
                isDisliked = videoPreferencesDataSource.isVideoDisliked(videoId),
                isSaved = videoPreferencesDataSource.isVideoSaved(videoId)
            )
            Result.success(mockVideo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recordInteraction(interaction: VideoInteraction): Result<Unit> {
        return try {
            videoPreferencesDataSource.recordInteraction(interaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(videoId: String): Result<VideoShort> {
        return try {
            videoPreferencesDataSource.toggleLike(videoId)
            getVideoById(videoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleDislike(videoId: String): Result<VideoShort> {
        return try {
            videoPreferencesDataSource.toggleDislike(videoId)
            getVideoById(videoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleSave(videoId: String): Result<VideoShort> {
        return try {
            videoPreferencesDataSource.toggleSave(videoId)
            getVideoById(videoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLikedVideos(): Flow<PagingData<VideoShort>> {
        // For now, return empty flow. In real implementation,
        // you would fetch liked videos from local database
        return flowOf(PagingData.empty())
    }

    override fun getSavedVideos(): Flow<PagingData<VideoShort>> {
        // For now, return empty flow. In real implementation,
        // you would fetch saved videos from local database
        return flowOf(PagingData.empty())
    }

    override fun searchVideos(query: String): Flow<PagingData<VideoShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                YouTubeShortsPagingSource(
                    youTubeApiService = youTubeApiService,
                    videoPreferencesDataSource = videoPreferencesDataSource,
                    searchQuery = query
                )
            }
        ).flow
    }

    override suspend fun getVideoStatistics(videoId: String): Result<Map<String, Long>> {
        return try {
            // Mock statistics for now
            val stats = mapOf(
                "viewCount" to 1000L,
                "likeCount" to 50L,
                "commentCount" to 10L
            )
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
