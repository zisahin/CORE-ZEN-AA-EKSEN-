package com.bysoftware.aaeksen.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bysoftware.aaeksen.data.local.VideoPreferencesDataSource
import com.bysoftware.aaeksen.data.mapper.VideoMapper.toDomain
import com.bysoftware.aaeksen.data.remote.YouTubeApiService
import com.bysoftware.aaeksen.domain.model.VideoShort
import retrofit2.HttpException
import java.io.IOException

class YouTubeShortsPagingSource(
    private val youTubeApiService: YouTubeApiService,
    private val videoPreferencesDataSource: VideoPreferencesDataSource,
    private val searchQuery: String? = null
) : PagingSource<String, VideoShort>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, VideoShort> {
        return try {
            val pageToken = params.key
            
            // Check if we have a valid API key
            if (com.bysoftware.aaeksen.core.constants.YouTubeConfig.API_KEY == "YOUR_YOUTUBE_API_KEY_HERE") {
                // Return mock data if no real API key is set
                val mockVideos = generateMockVideos(params.loadSize)
                return LoadResult.Page(
                    data = mockVideos,
                    prevKey = null,
                    nextKey = if (mockVideos.size < params.loadSize) null else "next_page_token"
                )
            }
            
            // Real API implementation
            val response = if (searchQuery != null) {
                youTubeApiService.searchVideosInChannel(
                    apiKey = com.bysoftware.aaeksen.core.constants.YouTubeConfig.API_KEY,
                    query = searchQuery,
                    maxResults = params.loadSize,
                    pageToken = pageToken
                )
            } else {
                youTubeApiService.searchChannelVideos(
                    apiKey = com.bysoftware.aaeksen.core.constants.YouTubeConfig.API_KEY,
                    channelId = com.bysoftware.aaeksen.core.constants.YouTubeConfig.ANADOLU_AJANSI_CHANNEL_ID,
                    maxResults = params.loadSize,
                    pageToken = pageToken
                )
            }

            // Debug: API response
            println("YouTube API Response: ${response.items.size} videos found")
            
            // Get video statistics for each video
            val videoIds = response.items.mapNotNull { it.id.videoId }.joinToString(",")
            println("Video IDs: $videoIds")
            
            val detailsResponse = if (videoIds.isNotEmpty()) {
                youTubeApiService.getVideoDetails(
                    apiKey = com.bysoftware.aaeksen.core.constants.YouTubeConfig.API_KEY,
                    videoIds = videoIds
                )
            } else null

            val videos = response.items.mapNotNull { dto ->
                val videoDetails = detailsResponse?.items?.find { it.id == dto.id.videoId }
                val domainVideo = dto.toDomain(videoDetails)
                
                // Apply user preferences
                domainVideo?.copy(
                    isLiked = videoPreferencesDataSource.isVideoLiked(domainVideo.id),
                    isDisliked = videoPreferencesDataSource.isVideoDisliked(domainVideo.id),
                    isSaved = videoPreferencesDataSource.isVideoSaved(domainVideo.id)
                )
            }
            
            println("Processed videos: ${videos.size}")

            LoadResult.Page(
                data = videos,
                prevKey = null, // YouTube API doesn't provide prevPageToken for search
                nextKey = response.nextPageToken
            )
        } catch (exception: IOException) {
            println("YouTube API IOException: ${exception.message}")
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            println("YouTube API HttpException: ${exception.code()} - ${exception.message()}")
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            println("YouTube API Exception: ${exception.message}")
            exception.printStackTrace()
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<String, VideoShort>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    private fun generateMockVideos(count: Int): List<VideoShort> {
        val realVideoIds = listOf(
            "UGr7y3QAVV0",
            "dQw4w9WgXcQ", 
            "jNQXAC9IVRw",
            "M7lc1UVf-VE",
            "YQHsXMglC9A"
        )
        
        return (1..count).map { index ->
            val videoId = realVideoIds[(index - 1) % realVideoIds.size]
            VideoShort(
                id = videoId,
                title = "Anadolu Ajansı Haber $index - Güncel Gelişmeler",
                description = "Bu güncel haber videosunda en son gelişmeleri sizlere aktarıyoruz. Detaylar için videoyu izleyin.",
                videoUrl = "https://www.youtube.com/watch?v=$videoId",
                thumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                duration = "PT${(15..60).random()}S",
                publishedAt = java.time.LocalDateTime.now().minusHours((1..24).random().toLong()),
                viewCount = (1000..100000).random().toLong(),
                likeCount = (50..5000).random().toLong(),
                channelId = "UCDmYRnhMwphO_BHiPhOLVzw",
                channelTitle = "Anadolu Ajansı",
                tags = listOf("haber", "güncel", "türkiye", "dünya"),
                isLiked = videoPreferencesDataSource.isVideoLiked(videoId),
                isDisliked = videoPreferencesDataSource.isVideoDisliked(videoId),
                isSaved = videoPreferencesDataSource.isVideoSaved(videoId)
            )
        }
    }
}
